package com.balancesheet.backend.service;

import com.balancesheet.backend.model.*;
import com.balancesheet.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, CustomerRepository customerRepository,
            CompanyRepository companyRepository, TransactionRepository transactionRepository,
            AccountRepository accountRepository) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.companyRepository = companyRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public List<Invoice> getAllInvoices(Long companyId) {
        return invoiceRepository.findByCompanyId(companyId);
    }

    @Transactional
    public Invoice createInvoice(Invoice invoice, Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        Customer customer = customerRepository.findById(invoice.getCustomer().getId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        invoice.setCompany(company);
        invoice.setCustomer(customer);
        invoice.setStatus(InvoiceStatus.DRAFT);

        // Calculate Total
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : invoice.getItems()) {
            item.setInvoice(invoice);
            BigDecimal lineTotal = item.getQuantity().multiply(item.getUnitPrice());
            item.setAmount(lineTotal);
            total = total.add(lineTotal);
        }
        invoice.setTotalAmount(total);

        // Auto-generate invoice number if not present (simple logic for now)
        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isEmpty()) {
            invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
        }

        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice approveInvoice(Long invoiceId, Long companyId, Long arAccountId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        if (!invoice.getCompany().getId().equals(companyId)) {
            throw new SecurityException("Unauthorized");
        }

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Invoice is not in DRAFT status");
        }

        // 1. Create Transaction
        Transaction transaction = new Transaction();
        transaction.setCompany(invoice.getCompany());
        transaction.setDate(invoice.getDate().atStartOfDay());
        transaction.setDescription("Invoice #" + invoice.getInvoiceNumber() + " - " + invoice.getCustomer().getName());
        transaction.setCurrency(invoice.getCurrency());

        // 2. Debit Accounts Receivable
        Account arAccount = accountRepository.findById(arAccountId)
                .orElseThrow(() -> new IllegalArgumentException("AR Account not found"));

        JournalEntry arEntry = new JournalEntry();
        arEntry.setAccount(arAccount);
        arEntry.setAmount(invoice.getTotalAmount()); // Debit is Positive
        arEntry.setTransaction(transaction);
        transaction.getEntries().add(arEntry);

        // 3. Credit Revenue Accounts (Line Items)
        for (InvoiceItem item : invoice.getItems()) {
            JournalEntry revenueEntry = new JournalEntry();
            revenueEntry.setAccount(item.getRevenueAccount());
            revenueEntry.setAmount(item.getAmount().negate()); // Credit is Negative
            revenueEntry.setTransaction(transaction);
            transaction.getEntries().add(revenueEntry);
        }

        transactionRepository.save(transaction);

        // 4. Update Invoice
        invoice.setStatus(InvoiceStatus.POSTED);
        invoice.setTransaction(transaction);

        return invoiceRepository.save(invoice);
    }
}

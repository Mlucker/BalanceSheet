package com.balancesheet.backend.service;

import com.balancesheet.backend.model.*;
import com.balancesheet.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final CompanyRepository companyRepository;

    public PaymentService(PaymentRepository paymentRepository, InvoiceRepository invoiceRepository,
            AccountRepository accountRepository, TransactionRepository transactionRepository,
            JournalEntryRepository journalEntryRepository, CompanyRepository companyRepository) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.journalEntryRepository = journalEntryRepository;
        this.companyRepository = companyRepository;
    }

    @Transactional
    public Payment recordPayment(Long companyId, Long invoiceId, BigDecimal amount, Long cashAccountId,
            LocalDate paymentDate, String paymentMethod, String reference) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));

        if (invoice.getStatus() != InvoiceStatus.POSTED) {
            throw new IllegalStateException("Can only record payments for POSTED invoices");
        }

        Account cashAccount = accountRepository.findById(cashAccountId)
                .orElseThrow(() -> new EntityNotFoundException("Cash account not found"));

        // Get the AR account from the original invoice transaction
        // We need to find which AR account was used when the invoice was posted
        Account arAccount = null;
        if (invoice.getTransaction() != null) {
            // Find the AR debit entry from the invoice posting
            List<JournalEntry> entries = journalEntryRepository.findByTransactionId(invoice.getTransaction().getId());
            for (JournalEntry entry : entries) {
                if (entry.getAmount().compareTo(BigDecimal.ZERO) > 0
                        && entry.getAccount().getType() == AccountType.ASSET) {
                    arAccount = entry.getAccount();
                    break;
                }
            }
        }

        if (arAccount == null) {
            throw new IllegalStateException("Could not find AR account from invoice");
        }

        // Create Transaction
        Transaction transaction = new Transaction();
        transaction.setDate(paymentDate.atStartOfDay());
        transaction.setDescription("Payment for Invoice #" + invoice.getInvoiceNumber() +
                (reference != null ? " - Ref: " + reference : ""));
        transaction.setCompany(company);
        transaction = transactionRepository.save(transaction);

        // Create Journal Entries
        // Debit Cash (increase asset)
        JournalEntry debitCash = new JournalEntry();
        debitCash.setTransaction(transaction);
        debitCash.setAccount(cashAccount);
        debitCash.setAmount(amount); // Positive = Debit
        journalEntryRepository.save(debitCash);

        // Credit AR (decrease asset)
        JournalEntry creditAR = new JournalEntry();
        creditAR.setTransaction(transaction);
        creditAR.setAccount(arAccount);
        creditAR.setAmount(amount.negate()); // Negative = Credit
        journalEntryRepository.save(creditAR);

        // Create Payment record
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(amount);
        payment.setPaymentDate(paymentDate);
        payment.setPaymentMethod(paymentMethod);
        payment.setReference(reference);
        payment.setCashAccount(cashAccount);
        payment.setCompany(company);
        payment.setTransaction(transaction);
        payment = paymentRepository.save(payment);

        // Update Invoice status
        BigDecimal totalPaid = getTotalPaidForInvoice(invoiceId);
        if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
            invoiceRepository.save(invoice);
        }

        return payment;
    }

    public BigDecimal getTotalPaidForInvoice(Long invoiceId) {
        List<Payment> payments = paymentRepository.findByInvoiceId(invoiceId);
        return payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Payment> getPaymentsByInvoice(Long invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId);
    }
}

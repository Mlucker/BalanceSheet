package com.balancesheet.backend.service;

import com.balancesheet.backend.dto.TransactionRequest;
import com.balancesheet.backend.model.*;
import com.balancesheet.backend.repository.AccountRepository;
import com.balancesheet.backend.repository.TransactionRepository;
import com.balancesheet.backend.repository.JournalEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;
import java.util.List;

@Service
public class AccountingService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final com.balancesheet.backend.repository.CompanyRepository companyRepository;

    public AccountingService(AccountRepository accountRepository, TransactionRepository transactionRepository,
            JournalEntryRepository journalEntryRepository,
            com.balancesheet.backend.repository.CompanyRepository companyRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.journalEntryRepository = journalEntryRepository;
        this.companyRepository = companyRepository;
    }

    public Account createAccount(Account account, Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));
        account.setCompany(company);
        return accountRepository.save(account);
    }

    public List<Account> getAllAccounts(Long companyId) {
        return accountRepository.findByCompanyId(companyId);
    }

    public List<Transaction> getAllTransactions(Long companyId, Integer year) {
        if (year != null) {
            return transactionRepository.findByCompanyIdAndYear(companyId, year);
        }
        return transactionRepository.findByCompanyIdOrderByDateDesc(companyId);
    }

    @Transactional
    public Transaction recordTransaction(TransactionRequest request, Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        BigDecimal total = request.getEntries().stream()
                .map(e -> e.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("Transaction must balance. Debits must equal Credits.");
        }

        Transaction transaction = new Transaction();
        transaction.setDescription(request.getDescription());
        transaction.setDate(request.getDate() != null ? request.getDate() : LocalDateTime.now());
        transaction.setCurrency(request.getCurrency() != null ? request.getCurrency() : company.getCurrency());
        transaction.setCompany(company);

        for (var entryReq : request.getEntries()) {
            Account account = accountRepository.findById(entryReq.getAccountId())
                    .orElseThrow(() -> new IllegalArgumentException("Account not found: " + entryReq.getAccountId()));

            // Verify account belongs to company
            if (!account.getCompany().getId().equals(companyId)) {
                throw new IllegalArgumentException("Account does not belong to this company");
            }

            JournalEntry entry = new JournalEntry();
            entry.setAccount(account);
            entry.setAmount(entryReq.getAmount());
            entry.setTransaction(transaction);
            transaction.getEntries().add(entry);
        }

        return transactionRepository.save(transaction);
    }

    public Map<AccountType, BigDecimal> getFinancialPosition(Long companyId, Integer year) {
        List<Object[]> results;
        if (year != null) {
            results = journalEntryRepository.sumByAccountTypeAndYear(companyId, year);
        } else {
            results = journalEntryRepository.sumByAccountType(companyId);
        }

        Map<AccountType, BigDecimal> map = new EnumMap<>(AccountType.class);
        // Initialize with ZERO
        for (AccountType type : AccountType.values()) {
            map.put(type, BigDecimal.ZERO);
        }

        for (Object[] result : results) {
            AccountType type = (AccountType) result[0];
            BigDecimal amount = (BigDecimal) result[1];
            if (type != null && amount != null) {
                map.put(type, amount);
            }
        }
        return map;
    }

    public Map<AccountType, List<Map<String, Object>>> getDetailedFinancialPosition(Long companyId, Integer year) {
        List<Object[]> results;
        if (year != null) {
            results = journalEntryRepository.sumByAccountAndYear(companyId, year);
        } else {
            results = journalEntryRepository.sumByAccount(companyId);
        }

        Map<AccountType, List<Map<String, Object>>> map = new EnumMap<>(AccountType.class);

        // Initialize lists
        for (AccountType type : AccountType.values()) {
            map.put(type, new java.util.ArrayList<>());
        }

        for (Object[] result : results) {
            Account account = (Account) result[0];
            BigDecimal amount = (BigDecimal) result[1];

            if (account != null && amount != null) {
                Map<String, Object> data = new java.util.HashMap<>();
                data.put("id", account.getId());
                data.put("name", account.getName());
                data.put("amount", amount);
                map.get(account.getType()).add(data);
            }
        }
        return map;
    }
}

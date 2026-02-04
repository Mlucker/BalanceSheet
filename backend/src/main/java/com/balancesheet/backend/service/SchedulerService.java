package com.balancesheet.backend.service;

import com.balancesheet.backend.model.JournalEntry;
import com.balancesheet.backend.model.RecurringTransaction;
import com.balancesheet.backend.model.Transaction;
import com.balancesheet.backend.repository.RecurringTransactionRepository;
import com.balancesheet.backend.repository.TransactionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SchedulerService {

    private final RecurringTransactionRepository recurringRepository;
    private final TransactionRepository transactionRepository;

    public SchedulerService(RecurringTransactionRepository recurringRepository,
            TransactionRepository transactionRepository) {
        this.recurringRepository = recurringRepository;
        this.transactionRepository = transactionRepository;
    }

    // Run every minute for demo purposes (in production, usually daily e.g., "0 0 0
    // * * ?")
    // For this prototype, we'll check every 10 seconds to make testing easier for
    // the user
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void processRecurringTransactions() {
        LocalDate today = LocalDate.now();
        List<RecurringTransaction> dueTransactions = recurringRepository.findByNextRunDateBefore(today.plusDays(1));

        for (RecurringTransaction rt : dueTransactions) {
            System.out.println("Processing recurring transaction: " + rt.getName());

            // Check if active (startDate <= today <= endDate)
            boolean isActive = true;
            if (rt.getStartDate() != null && today.isBefore(rt.getStartDate())) {
                isActive = false;
            }
            if (rt.getEndDate() != null && today.isAfter(rt.getEndDate())) {
                isActive = false;
            }

            if (isActive) {
                Transaction tx = new Transaction();
                tx.setDate(LocalDateTime.now());
                tx.setDescription("Auto: " + rt.getDescription() + " (" + rt.getName() + ")");
                tx.setCompany(rt.getCompany());
                tx.setCurrency(rt.getCompany().getCurrency()); // Set currency from company

                // Debit Entry
                JournalEntry dr = new JournalEntry();
                dr.setAccount(rt.getDebitAccount());
                dr.setAmount(rt.getAmount()); // Positive for Debit
                dr.setTransaction(tx);
                tx.getEntries().add(dr);

                // Credit Entry
                JournalEntry cr = new JournalEntry();
                cr.setAccount(rt.getCreditAccount());
                cr.setAmount(rt.getAmount().negate()); // Negative for Credit
                cr.setTransaction(tx);
                tx.getEntries().add(cr);

                transactionRepository.save(tx);
            } else {
                System.out.println("Skipping item " + rt.getName() + " (Inactive date range)");
            }

            // Update next run date to next month
            // If day of month is > 28, handle carefully, but for simple MVP just plusDays
            // or plusMonths
            LocalDate nextDate = rt.getNextRunDate().plusMonths(1);

            // Should skipping also advance? Yes, normally, to check again next month.
            // But if it's inactive because it ended, it simply won't matter.
            // If it's inactive because it hasn't started yet, we need to advance or set to
            // startDate?
            // Simple logic: Always advance so we don't get stuck in a loop if we run every
            // minute.

            // Optimization: If startDate is far in future, maybe set nextRunDate to
            // startDate?
            // For now, keep simple behavior:
            rt.setNextRunDate(nextDate);
            recurringRepository.save(rt);
        }
    }
}

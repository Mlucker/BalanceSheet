package com.balancesheet.backend.service;

import com.balancesheet.backend.model.*;
import com.balancesheet.backend.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CashFlowService {

    private final JournalEntryRepository journalEntryRepository;
    private final InvoiceRepository invoiceRepository;
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final AccountRepository accountRepository;

    public CashFlowService(JournalEntryRepository journalEntryRepository, InvoiceRepository invoiceRepository,
            RecurringTransactionRepository recurringTransactionRepository, AccountRepository accountRepository) {
        this.journalEntryRepository = journalEntryRepository;
        this.invoiceRepository = invoiceRepository;
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.accountRepository = accountRepository;
    }

    // Historical Cash Balance (Last 30 Days)
    public List<Map<String, Object>> getCashBalanceHistory(Long companyId) {
        // 1. Get all ASSET accounts that are likely Cash/Bank.
        // For MVP, we'll take all ASSET accounts or filter by name "Cash", "Bank".
        // Let's rely on ASSET type for now.
        List<Account> cashAccounts = accountRepository.findByCompanyId(companyId).stream()
                .filter(a -> a.getType() == AccountType.ASSET)
                .collect(Collectors.toList());

        List<Long> accountIds = cashAccounts.stream().map(Account::getId).collect(Collectors.toList());

        // 2. Get all journal entries for these accounts ordered by date
        // Note: scalable solution would use daily aggregates, but this works for MVP
        // volumes
        List<JournalEntry> entries = journalEntryRepository.findAll().stream()
                .filter(e -> accountIds.contains(e.getAccount().getId()))
                .sorted(Comparator.comparing(e -> e.getTransaction().getDate()))
                .collect(Collectors.toList());

        // 3. Build daily balances
        // We'll calculate the cumulative balance up to today, then map it to dates.
        // Actually, easier to compute total balance NOW, then work backwards?
        // Or just re-simulate from 0. Let's re-simulate from 0 for simplicity.

        Map<LocalDate, BigDecimal> dailyBalance = new TreeMap<>();

        // Populate map with 0 for last 30 days initially to ensure no gaps
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 30; i++) {
            dailyBalance.put(today.minusDays(i), BigDecimal.ZERO);
        }

        // Replay history
        // Previous loop removed for optimization (redundant calculation)

        // Re-do: We need the balance *AT THE END* of specific dates.
        // Let's optimize:
        // 1. Get Total Current Balance.
        // 2. Group last 30 days transactions by date.
        // 3. Work backwards from Current Balance.

        // Recalculate Total Current
        BigDecimal totalCurrent = BigDecimal.ZERO;
        for (JournalEntry e : entries) {
            totalCurrent = totalCurrent.add(e.getAmount());
        }

        List<Map<String, Object>> history = new ArrayList<>();
        LocalDate cursor = LocalDate.now();
        BigDecimal runningBalance = totalCurrent;

        for (int i = 0; i < 30; i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("date", cursor.toString());
            point.put("balance", runningBalance);
            history.add(point);

            // Undo transactions for this day to get yesterday's closing balance
            final LocalDate currentDate = cursor;
            BigDecimal dayChange = entries.stream()
                    .filter(e -> e.getTransaction().getDate().toLocalDate().equals(currentDate))
                    .map(JournalEntry::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            runningBalance = runningBalance.subtract(dayChange);
            cursor = cursor.minusDays(1);
        }

        // Sort chronologically for chart
        Collections.reverse(history);
        return history;
    }

    // Forecast (Next 30 Days)
    public Map<String, Object> getForecast(Long companyId) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(30);

        // 1. Inflows (Unpaid Invoices Due)
        List<Invoice> unpaidInvoices = invoiceRepository.findByCompanyId(companyId).stream()
                .filter(inv -> inv.getStatus() == InvoiceStatus.POSTED) // Sent but not PAID
                .filter(inv -> !inv.getDueDate().isBefore(today) && !inv.getDueDate().isAfter(endDate))
                .collect(Collectors.toList());

        BigDecimal totalInflow = unpaidInvoices.stream()
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Outflows (Recurring Transactions)
        List<RecurringTransaction> recurring = recurringTransactionRepository.findAll().stream()
                .filter(rt -> rt.getCompany().getId().equals(companyId))
                .collect(Collectors.toList());

        BigDecimal totalOutflow = BigDecimal.ZERO;
        List<Map<String, Object>> outflowDetails = new ArrayList<>();

        for (RecurringTransaction rt : recurring) {
            // Check if it runs in window
            LocalDate nextRun = rt.getNextRunDate();
            if (nextRun != null && !nextRun.isBefore(today) && !nextRun.isAfter(endDate)) {
                totalOutflow = totalOutflow.add(rt.getAmount());

                // Add to details
                Map<String, Object> detail = new HashMap<>();
                detail.put("name", rt.getName());
                detail.put("date", nextRun.toString());
                detail.put("amount", rt.getAmount());
                outflowDetails.add(detail);

                // Note: If frequency is < 30 days, we should check subsequent runs.
                // Assuming monthly for now (based on dayOfMonth field).
            }
        }

        Map<String, Object> forecast = new HashMap<>();
        forecast.put("totalInflow", totalInflow);
        forecast.put("totalOutflow", totalOutflow);
        forecast.put("invoices", unpaidInvoices.stream().map(i -> Map.of("customer", i.getCustomer().getName(),
                "amount", i.getTotalAmount(), "dueDate", i.getDueDate())).collect(Collectors.toList()));
        forecast.put("recurringExpenses", outflowDetails);

        return forecast;
    }
}

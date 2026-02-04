package com.balancesheet.backend.service;

import com.balancesheet.backend.model.Account;
import com.balancesheet.backend.model.AccountType;
import com.balancesheet.backend.model.JournalEntry;
import com.balancesheet.backend.repository.JournalEntryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReportService {

    private final JournalEntryRepository journalEntryRepository;

    public ReportService(JournalEntryRepository journalEntryRepository) {
        this.journalEntryRepository = journalEntryRepository;
    }

    public Map<String, Object> getProfitAndLoss(Long companyId, int year) {
        LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, 12, 31, 23, 59, 59);

        // Supports comparative previous year query
        LocalDateTime prevStartDate = startDate.minusYears(1);
        LocalDateTime prevEndDate = endDate.minusYears(1);

        Map<String, Object> report = new HashMap<>();
        report.put("year", year);
        report.put("current", calculatePnL(companyId, startDate, endDate));
        report.put("previous", calculatePnL(companyId, prevStartDate, prevEndDate));

        return report;
    }

    private Map<String, BigDecimal> calculatePnL(Long companyId, LocalDateTime start, LocalDateTime end) {
        List<Object[]> results = journalEntryRepository.sumByAccountTypeAndDateRange(companyId, start, end);

        BigDecimal revenue = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;

        for (Object[] row : results) {
            AccountType type = (AccountType) row[0];
            BigDecimal amount = (BigDecimal) row[1];

            // In DB: Revenue is Credit (-), Expense is Debit (+)
            // For P&L display: We want Revenue positive, Expense positive
            if (type == AccountType.REVENUE)
                revenue = revenue.add(amount.abs());
            if (type == AccountType.EXPENSE)
                expense = expense.add(amount.abs());
        }

        Map<String, BigDecimal> data = new HashMap<>();
        data.put("revenue", revenue);
        data.put("expense", expense);
        data.put("netIncome", revenue.subtract(expense));
        return data;
    }

    public List<Map<String, Object>> getTrialBalance(Long companyId) {
        // Trial Balance is "As of today" usually, spanning all time
        // Or we can verify 0
        List<Object[]> results = journalEntryRepository.sumByAccount(companyId);
        List<Map<String, Object>> report = new ArrayList<>();

        for (Object[] row : results) {
            Account account = (Account) row[0];
            BigDecimal balance = (BigDecimal) row[1];

            // Skip zero balance accounts if desired, but traditionally showed
            if (balance.compareTo(BigDecimal.ZERO) == 0)
                continue;

            Map<String, Object> item = new HashMap<>();
            item.put("account", account.getName());
            item.put("type", account.getType());

            // Debit or Credit column
            if (balance.compareTo(BigDecimal.ZERO) >= 0) {
                item.put("debit", balance);
                item.put("credit", BigDecimal.ZERO);
            } else {
                item.put("debit", BigDecimal.ZERO);
                item.put("credit", balance.abs());
            }
            report.add(item);
        }
        return report;
    }

    public List<JournalEntry> getGeneralLedger(Long companyId, Long accountId, int year) {
        LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, 12, 31, 23, 59, 59);
        return journalEntryRepository.findByAccountAndDateRange(companyId, accountId, startDate, endDate);
    }
}

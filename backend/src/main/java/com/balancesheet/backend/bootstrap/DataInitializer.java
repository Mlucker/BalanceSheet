package com.balancesheet.backend.bootstrap;

import com.balancesheet.backend.model.Account;
import com.balancesheet.backend.model.AccountType;
import com.balancesheet.backend.model.Company;
import com.balancesheet.backend.repository.AccountRepository;
import com.balancesheet.backend.repository.CompanyRepository;
import com.balancesheet.backend.model.JournalEntry;
import com.balancesheet.backend.model.Transaction;
import com.balancesheet.backend.repository.TransactionRepository;
import com.balancesheet.backend.model.RecurringTransaction;
import com.balancesheet.backend.repository.RecurringTransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

        private final AccountRepository accountRepository;
        private final CompanyRepository companyRepository;
        private final TransactionRepository transactionRepository;
        private final RecurringTransactionRepository recurringTransactionRepository;

        public DataInitializer(AccountRepository accountRepository, CompanyRepository companyRepository,
                        TransactionRepository transactionRepository,
                        RecurringTransactionRepository recurringTransactionRepository) {
                this.accountRepository = accountRepository;
                this.companyRepository = companyRepository;
                this.transactionRepository = transactionRepository;
                this.recurringTransactionRepository = recurringTransactionRepository;
        }

        @Override
        public void run(String... args) throws Exception {
                if (companyRepository.count() == 0) {
                        // 1. Create Companies
                        Company demoCompany = new Company(null, "Demo Brewery", "EUR");
                        demoCompany = companyRepository.save(demoCompany);

                        Company myCompany = new Company(null, "My Company", "USD");
                        myCompany = companyRepository.save(myCompany);

                        // 2. Initialize "My Company" (Clean State)
                        createDefaultAccounts(myCompany);

                        // 3. Initialize "Demo Company" (With Data)
                        Map<String, Account> demoAccounts = createBreweryAccounts(demoCompany);
                        seedBreweryAutomations(demoCompany, demoAccounts);
                        seedDemoTransactions(demoCompany, demoAccounts);

                        System.out.println("Companies and Default accounts initialized.");
                }
        }

        private Map<String, Account> createDefaultAccounts(Company company) {
                // Standard accounts for "My Company"
                Account cash = new Account(null, "Cash", AccountType.ASSET, company);
                Account ar = new Account(null, "Accounts Receivable", AccountType.ASSET, company);
                Account ap = new Account(null, "Accounts Payable", AccountType.LIABILITY, company);
                Account stock = new Account(null, "Common Stock", AccountType.EQUITY, company);
                Account retainedEarnings = new Account(null, "Retained Earnings", AccountType.EQUITY, company);
                Account sales = new Account(null, "Sales Revenue", AccountType.REVENUE, company);
                Account salaries = new Account(null, "Salaries Expense", AccountType.EXPENSE, company);
                Account rent = new Account(null, "Rent Expense", AccountType.EXPENSE, company);
                Account maintenance = new Account(null, "Maintenance Expense", AccountType.EXPENSE, company);

                List<Account> accounts = Arrays.asList(cash, ar, ap, stock, retainedEarnings, sales, salaries, rent,
                                maintenance);
                accountRepository.saveAll(accounts);

                return accounts.stream()
                                .collect(java.util.stream.Collectors.toMap(Account::getName, account -> account));
        }

        private Map<String, Account> createBreweryAccounts(Company company) {
                Account cash = new Account(null, "Cash", AccountType.ASSET, company);
                Account ar = new Account(null, "Accounts Receivable", AccountType.ASSET, company);
                Account ap = new Account(null, "Accounts Payable", AccountType.LIABILITY, company);
                Account stock = new Account(null, "Common Stock", AccountType.EQUITY, company);
                Account retainedEarnings = new Account(null, "Retained Earnings", AccountType.EQUITY, company);

                // Brewery Specific
                Account beerSales = new Account(null, "Beer Sales Revenue", AccountType.REVENUE, company);
                Account taproomSales = new Account(null, "Taproom Revenue", AccountType.REVENUE, company);

                Account ingredients = new Account(null, "Brewing Supplies Expense", AccountType.EXPENSE, company);
                Account salaries = new Account(null, "Salaries Expense", AccountType.EXPENSE, company);
                Account rent = new Account(null, "Rent Expense", AccountType.EXPENSE, company);
                Account maintenance = new Account(null, "Maintenance Expense", AccountType.EXPENSE, company);
                Account marketing = new Account(null, "Marketing Expense", AccountType.EXPENSE, company);
                Account utilities = new Account(null, "Utilities Expense", AccountType.EXPENSE, company);
                Account insurance = new Account(null, "Insurance Expense", AccountType.EXPENSE, company);

                List<Account> accounts = Arrays.asList(cash, ar, ap, stock, retainedEarnings, beerSales, taproomSales,
                                salaries, rent, maintenance, marketing, utilities, insurance, ingredients);
                accountRepository.saveAll(accounts);

                return accounts.stream()
                                .collect(java.util.stream.Collectors.toMap(Account::getName, account -> account));
        }

        private void seedDemoTransactions(Company company, Map<String, Account> accounts) {
                // --- 2024: The Start-up of "Demo Brewery" ---
                createTransaction(company, LocalDateTime.of(2024, 1, 10, 10, 0), "Initial Investment",
                                new JournalEntry(null, null, accounts.get("Cash"), new BigDecimal("100000.00")),
                                new JournalEntry(null, null, accounts.get("Common Stock"),
                                                new BigDecimal("-100000.00")));

                createTransaction(company, LocalDateTime.of(2024, 2, 1, 9, 0), "Brewery Lease - Q1",
                                new JournalEntry(null, null, accounts.get("Rent Expense"), new BigDecimal("10500.00")),
                                new JournalEntry(null, null, accounts.get("Cash"), new BigDecimal("-10500.00")));

                createTransaction(company, LocalDateTime.of(2024, 3, 15, 14, 30), "First Batch: Ingredients",
                                new JournalEntry(null, null, accounts.get("Brewing Supplies Expense"),
                                                new BigDecimal("4500.00")),
                                new JournalEntry(null, null, accounts.get("Cash"), new BigDecimal("-4500.00")));

                createTransaction(company, LocalDateTime.of(2024, 6, 20, 11, 0), "Grand Opening Marketing",
                                new JournalEntry(null, null, accounts.get("Marketing Expense"),
                                                new BigDecimal("8000.00")),
                                new JournalEntry(null, null, accounts.get("Cash"), new BigDecimal("-8000.00")));

                // --- 2025: Production & Sales ---
                // Rent
                for (int month = 1; month <= 12; month++) {
                        createTransaction(company, LocalDateTime.of(2025, month, 1, 9, 0), "Monthly Brewery Rent",
                                        new JournalEntry(null, null, accounts.get("Rent Expense"),
                                                        new BigDecimal("3500.00")),
                                        new JournalEntry(null, null, accounts.get("Cash"), new BigDecimal("-3500.00")));
                }

                // Quarterly Distributor Sales (Beer Sales)
                createTransaction(company, LocalDateTime.of(2025, 3, 30, 15, 0), "Q1 Distributor Sales",
                                new JournalEntry(null, null, accounts.get("Cash"), new BigDecimal("45000.00")),
                                new JournalEntry(null, null, accounts.get("Beer Sales Revenue"),
                                                new BigDecimal("-45000.00")));

                createTransaction(company, LocalDateTime.of(2025, 6, 30, 15, 0), "Q2 Distributor Sales",
                                new JournalEntry(null, null, accounts.get("Cash"), new BigDecimal("58000.00")),
                                new JournalEntry(null, null, accounts.get("Beer Sales Revenue"),
                                                new BigDecimal("-58000.00")));

                createTransaction(company, LocalDateTime.of(2025, 9, 30, 15, 0), "Q3 Distributor Sales",
                                new JournalEntry(null, null, accounts.get("Cash"), new BigDecimal("62000.00")),
                                new JournalEntry(null, null, accounts.get("Beer Sales Revenue"),
                                                new BigDecimal("-62000.00")));

                createTransaction(company, LocalDateTime.of(2025, 12, 15, 15, 0), "Q4 Holiday Sales",
                                new JournalEntry(null, null, accounts.get("Cash"), new BigDecimal("75000.00")),
                                new JournalEntry(null, null, accounts.get("Beer Sales Revenue"),
                                                new BigDecimal("-75000.00")));

                // Taproom Revenue (Monthly equivalent for simplicity, or just some erratic
                // entries)
                createTransaction(company, LocalDateTime.of(2025, 12, 31, 23, 0), "Total 2025 Taproom Sales",
                                new JournalEntry(null, null, accounts.get("Cash"), new BigDecimal("120000.00")), // $10k/month
                                                                                                                 // avg
                                new JournalEntry(null, null, accounts.get("Taproom Revenue"),
                                                new BigDecimal("-120000.00")));

                // New Fermentation Tanks
                createTransaction(company, LocalDateTime.of(2025, 7, 10, 10, 0), "New Fermentation Tanks (x2)",
                                new JournalEntry(null, null, accounts.get("Maintenance Expense"),
                                                new BigDecimal("12000.00")), // Expensing for simplicity, or could be
                                                                             // Asset
                                new JournalEntry(null, null, accounts.get("Cash"), new BigDecimal("-12000.00")));

                // --- 2026: Current Year (YTD) ---
                // Rent (Jan, Feb, March)
                for (int month = 1; month <= 3; month++) {
                        createTransaction(company, LocalDateTime.of(2026, month, 1, 9, 0), "Monthly Brewery Rent",
                                        new JournalEntry(null, null, accounts.get("Rent Expense"),
                                                        new BigDecimal("3500.00")),
                                        new JournalEntry(null, null, accounts.get("Cash"), new BigDecimal("-3500.00")));
                }

                // Ingredients Restock
                createTransaction(company, LocalDateTime.of(2026, 1, 15, 10, 0), "Bulk Hops & Malt Order",
                                new JournalEntry(null, null, accounts.get("Brewing Supplies Expense"),
                                                new BigDecimal("8500.00")),
                                new JournalEntry(null, null, accounts.get("Cash"), new BigDecimal("-8500.00")));

                // Taproom Sales Jan
                createTransaction(company, LocalDateTime.of(2026, 1, 31, 22, 0), "Jan Taproom Sales",
                                new JournalEntry(null, null, accounts.get("Cash"), new BigDecimal("11500.00")),
                                new JournalEntry(null, null, accounts.get("Taproom Revenue"),
                                                new BigDecimal("-11500.00")));

                // Taproom Sales Feb
                createTransaction(company, LocalDateTime.of(2026, 2, 28, 22, 0), "Feb Taproom Sales",
                                new JournalEntry(null, null, accounts.get("Cash"), new BigDecimal("10800.00")),
                                new JournalEntry(null, null, accounts.get("Taproom Revenue"),
                                                new BigDecimal("-10800.00")));
        }

        private void createTransaction(Company company, LocalDateTime date, String description,
                        JournalEntry... entries) {
                Transaction transaction = new Transaction();
                transaction.setCompany(company);
                transaction.setDate(date);
                transaction.setDescription(description);
                transaction.setCurrency(company.getCurrency());

                for (JournalEntry entry : entries) {
                        entry.setTransaction(transaction);
                        transaction.getEntries().add(entry);
                }

                transactionRepository.save(transaction);
        }

        private void seedBreweryAutomations(Company company, Map<String, Account> accounts) {
                // 1. Team Members
                createRecurringTransaction(company, "Head Brewer (Hans)", "Monthly Salary", new BigDecimal("4500.00"),
                                28,
                                accounts.get("Salaries Expense"), accounts.get("Cash"), "TEAM",
                                LocalDate.of(2023, 1, 1), null);

                createRecurringTransaction(company, "Taproom Manager (Sarah)", "Monthly Salary",
                                new BigDecimal("3200.00"), 28,
                                accounts.get("Salaries Expense"), accounts.get("Cash"), "TEAM",
                                LocalDate.of(2023, 6, 1), null);

                createRecurringTransaction(company, "Assistant Brewer (Mike)", "Monthly Salary",
                                new BigDecimal("2800.00"), 28,
                                accounts.get("Salaries Expense"), accounts.get("Cash"), "TEAM",
                                LocalDate.of(2024, 3, 15), null);

                // 2. Buildings (Rent)
                createRecurringTransaction(company, "Main Brewery & Taproom", "Monthly Rent", new BigDecimal("3500.00"),
                                1,
                                accounts.get("Rent Expense"), accounts.get("Cash"), "BUILDING",
                                LocalDate.of(2023, 1, 1), null);

                createRecurringTransaction(company, "Storage Warehouse", "Monthly Rent", new BigDecimal("1200.00"), 1,
                                accounts.get("Rent Expense"), accounts.get("Cash"), "BUILDING",
                                LocalDate.of(2024, 1, 1), null);

                // 3. Machines (Maintenance)
                createRecurringTransaction(company, "Fermentation Tanks (x4)", "Monthly Maintenance",
                                new BigDecimal("600.00"), 15,
                                accounts.get("Maintenance Expense"), accounts.get("Cash"), "MACHINE",
                                LocalDate.of(2023, 2, 1), null);

                createRecurringTransaction(company, "Bottling Line", "Monthly Servicing", new BigDecimal("450.00"), 20,
                                accounts.get("Maintenance Expense"), accounts.get("Cash"), "MACHINE",
                                LocalDate.of(2023, 8, 1), null);
        }

        private void createRecurringTransaction(Company company, String name, String description, BigDecimal amount,
                        int dayOfMonth, Account debitAccount, Account creditAccount, String category,
                        LocalDate startDate,
                        LocalDate endDate) {
                RecurringTransaction rt = new RecurringTransaction();
                rt.setName(name);
                rt.setDescription(description);
                rt.setAmount(amount);
                rt.setDayOfMonth(dayOfMonth);
                rt.setDebitAccount(debitAccount);
                rt.setCreditAccount(creditAccount);
                rt.setCategory(category);
                rt.setCompany(company);
                rt.setStartDate(startDate);
                rt.setEndDate(endDate);

                // Set next run date to next occurrence of dayOfMonth
                LocalDate today = LocalDate.now();
                LocalDate nextRun = today.withDayOfMonth(dayOfMonth);
                if (nextRun.isBefore(today) || nextRun.isEqual(today)) {
                        nextRun = nextRun.plusMonths(1);
                }
                rt.setNextRunDate(nextRun);

                recurringTransactionRepository.save(rt);
        }
}

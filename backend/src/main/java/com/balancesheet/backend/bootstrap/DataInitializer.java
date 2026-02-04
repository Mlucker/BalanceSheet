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
@lombok.extern.slf4j.Slf4j
public class DataInitializer implements CommandLineRunner {

        private final AccountRepository accountRepository;
        private final CompanyRepository companyRepository;
        private final TransactionRepository transactionRepository;
        private final RecurringTransactionRepository recurringTransactionRepository;
        private final com.balancesheet.backend.repository.CustomerRepository customerRepository;
        private final com.balancesheet.backend.repository.ProductRepository productRepository;
        private final com.balancesheet.backend.repository.InvoiceRepository invoiceRepository;
        private final com.balancesheet.backend.repository.PaymentRepository paymentRepository;
        private final com.balancesheet.backend.repository.TransactionTemplateRepository templateRepository;

        public DataInitializer(AccountRepository accountRepository, CompanyRepository companyRepository,
                        TransactionRepository transactionRepository,
                        RecurringTransactionRepository recurringTransactionRepository,
                        com.balancesheet.backend.repository.CustomerRepository customerRepository,
                        com.balancesheet.backend.repository.ProductRepository productRepository,
                        com.balancesheet.backend.repository.InvoiceRepository invoiceRepository,
                        com.balancesheet.backend.repository.PaymentRepository paymentRepository,
                        com.balancesheet.backend.repository.TransactionTemplateRepository templateRepository) {
                this.accountRepository = accountRepository;
                this.companyRepository = companyRepository;
                this.transactionRepository = transactionRepository;
                this.recurringTransactionRepository = recurringTransactionRepository;
                this.customerRepository = customerRepository;
                this.productRepository = productRepository;
                this.invoiceRepository = invoiceRepository;
                this.paymentRepository = paymentRepository;
                this.templateRepository = templateRepository;
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
                        seedDemoCustomers(demoCompany);
                        seedDemoProducts(demoCompany);
                        seedDemoInvoices(demoCompany, demoAccounts);
                        seedDemoTemplates(demoCompany, demoAccounts);

                        log.info("Companies and Default accounts initialized.");
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

        private void seedDemoCustomers(Company company) {
                com.balancesheet.backend.model.Customer c1 = new com.balancesheet.backend.model.Customer();
                c1.setName("Hoppy Distributors Inc.");
                c1.setEmail("orders@hoppydist.com");
                c1.setPhone("+1-555-0101");
                c1.setAddress("123 Beer Street, Portland, OR 97201");
                c1.setCompany(company);
                customerRepository.save(c1);

                com.balancesheet.backend.model.Customer c2 = new com.balancesheet.backend.model.Customer();
                c2.setName("Craft Beer Emporium");
                c2.setEmail("purchasing@craftemporium.com");
                c2.setPhone("+1-555-0202");
                c2.setAddress("456 Ale Avenue, Seattle, WA 98101");
                c2.setCompany(company);
                customerRepository.save(c2);

                com.balancesheet.backend.model.Customer c3 = new com.balancesheet.backend.model.Customer();
                c3.setName("Local Pub Chain");
                c3.setEmail("orders@localpubs.com");
                c3.setPhone("+1-555-0303");
                c3.setAddress("789 Lager Lane, Denver, CO 80202");
                c3.setCompany(company);
                customerRepository.save(c3);

                com.balancesheet.backend.model.Customer c4 = new com.balancesheet.backend.model.Customer();
                c4.setName("Mountain View Tavern");
                c4.setEmail("manager@mountainviewtavern.com");
                c4.setPhone("+1-555-0404");
                c4.setAddress("321 Summit Road, Boulder, CO 80301");
                c4.setCompany(company);
                customerRepository.save(c4);

                com.balancesheet.backend.model.Customer c5 = new com.balancesheet.backend.model.Customer();
                c5.setName("Pacific Northwest Beverages");
                c5.setEmail("procurement@pnwbev.com");
                c5.setPhone("+1-555-0505");
                c5.setAddress("555 Coast Highway, Eugene, OR 97401");
                c5.setCompany(company);
                customerRepository.save(c5);

                com.balancesheet.backend.model.Customer c6 = new com.balancesheet.backend.model.Customer();
                c6.setName("Brewery Direct Wholesale");
                c6.setEmail("sales@brewerydirect.com");
                c6.setPhone("+1-555-0606");
                c6.setAddress("888 Industrial Blvd, Tacoma, WA 98402");
                c6.setCompany(company);
                customerRepository.save(c6);
        }

        private void seedDemoProducts(Company company) {
                com.balancesheet.backend.model.Product p1 = new com.balancesheet.backend.model.Product();
                p1.setName("Hoppy IPA");
                p1.setSku("BEER-IPA-001");
                p1.setDescription("Our flagship India Pale Ale with citrus notes");
                p1.setSellingPrice(new BigDecimal("45.00"));
                p1.setCostPrice(new BigDecimal("18.00"));
                p1.setQuantityOnHand(250);
                p1.setCompany(company);
                productRepository.save(p1);

                com.balancesheet.backend.model.Product p2 = new com.balancesheet.backend.model.Product();
                p2.setName("Dark Stout");
                p2.setSku("BEER-STOUT-001");
                p2.setDescription("Rich, creamy stout with coffee undertones");
                p2.setSellingPrice(new BigDecimal("50.00"));
                p2.setCostPrice(new BigDecimal("20.00"));
                p2.setQuantityOnHand(180);
                p2.setCompany(company);
                productRepository.save(p2);

                com.balancesheet.backend.model.Product p3 = new com.balancesheet.backend.model.Product();
                p3.setName("Summer Wheat");
                p3.setSku("BEER-WHEAT-001");
                p3.setDescription("Light and refreshing wheat beer");
                p3.setSellingPrice(new BigDecimal("40.00"));
                p3.setCostPrice(new BigDecimal("16.00"));
                p3.setQuantityOnHand(320);
                p3.setCompany(company);
                productRepository.save(p3);

                com.balancesheet.backend.model.Product p4 = new com.balancesheet.backend.model.Product();
                p4.setName("Amber Lager");
                p4.setSku("BEER-LAGER-001");
                p4.setDescription("Smooth amber lager with caramel malt flavor");
                p4.setSellingPrice(new BigDecimal("42.00"));
                p4.setCostPrice(new BigDecimal("17.00"));
                p4.setQuantityOnHand(200);
                p4.setCompany(company);
                productRepository.save(p4);

                com.balancesheet.backend.model.Product p5 = new com.balancesheet.backend.model.Product();
                p5.setName("Pale Ale");
                p5.setSku("BEER-PALE-001");
                p5.setDescription("Classic American pale ale with balanced hops");
                p5.setSellingPrice(new BigDecimal("43.00"));
                p5.setCostPrice(new BigDecimal("17.50"));
                p5.setQuantityOnHand(275);
                p5.setCompany(company);
                productRepository.save(p5);

                com.balancesheet.backend.model.Product p6 = new com.balancesheet.backend.model.Product();
                p6.setName("Porter");
                p6.setSku("BEER-PORTER-001");
                p6.setDescription("Robust porter with chocolate and roasted malt");
                p6.setSellingPrice(new BigDecimal("48.00"));
                p6.setCostPrice(new BigDecimal("19.00"));
                p6.setQuantityOnHand(150);
                p6.setCompany(company);
                productRepository.save(p6);
        }

        private void seedDemoInvoices(Company company, Map<String, Account> accounts) {
                java.util.List<com.balancesheet.backend.model.Customer> customers = customerRepository
                                .findByCompanyId(company.getId());
                java.util.List<com.balancesheet.backend.model.Product> products = productRepository
                                .findByCompanyId(company.getId());

                if (customers.isEmpty() || products.isEmpty())
                        return;

                Account arAccount = accounts.get("Accounts Receivable");
                Account beerSalesAccount = accounts.get("Beer Sales Revenue");
                Account cashAccount = accounts.get("Cash");

                // Invoice 1: PAID (from last month)
                com.balancesheet.backend.model.Invoice inv1 = new com.balancesheet.backend.model.Invoice();
                inv1.setInvoiceNumber("INV-2026-001");
                inv1.setCustomer(customers.get(0));
                inv1.setDate(LocalDate.of(2026, 1, 15));
                inv1.setDueDate(LocalDate.of(2026, 2, 14));
                inv1.setStatus(com.balancesheet.backend.model.InvoiceStatus.PAID);
                inv1.setCurrency(company.getCurrency());
                inv1.setCompany(company);

                com.balancesheet.backend.model.InvoiceItem item1 = new com.balancesheet.backend.model.InvoiceItem();
                item1.setDescription(products.get(0).getName());
                item1.setQuantity(new BigDecimal("50"));
                item1.setUnitPrice(products.get(0).getSellingPrice());
                item1.setAmount(new BigDecimal("2250.00"));
                item1.setRevenueAccount(beerSalesAccount);
                item1.setProduct(products.get(0));
                item1.setInvoice(inv1);
                inv1.getItems().add(item1);
                inv1.setTotalAmount(new BigDecimal("2250.00"));

                // Create transaction for posting
                Transaction tx1 = new Transaction();
                tx1.setDate(LocalDate.of(2026, 1, 15).atStartOfDay());
                tx1.setDescription("Invoice #" + inv1.getInvoiceNumber());
                tx1.setCompany(company);
                tx1.setCurrency(company.getCurrency());
                JournalEntry debit1 = new JournalEntry(null, tx1, arAccount, new BigDecimal("2250.00"));
                JournalEntry credit1 = new JournalEntry(null, tx1, beerSalesAccount, new BigDecimal("-2250.00"));
                tx1.getEntries().add(debit1);
                tx1.getEntries().add(credit1);
                transactionRepository.save(tx1);
                inv1.setTransaction(tx1);
                invoiceRepository.save(inv1);

                // Create payment for invoice 1
                com.balancesheet.backend.model.Payment payment1 = new com.balancesheet.backend.model.Payment();
                payment1.setInvoice(inv1);
                payment1.setAmount(new BigDecimal("2250.00"));
                payment1.setPaymentDate(LocalDate.of(2026, 1, 25));
                payment1.setPaymentMethod("Bank Transfer");
                payment1.setReference("TRF-20260125-001");
                payment1.setCashAccount(cashAccount);
                payment1.setCompany(company);

                Transaction payTx1 = new Transaction();
                payTx1.setDate(LocalDate.of(2026, 1, 25).atStartOfDay());
                payTx1.setDescription("Payment for Invoice #" + inv1.getInvoiceNumber());
                payTx1.setCompany(company);
                payTx1.setCurrency(company.getCurrency());
                JournalEntry payDebit1 = new JournalEntry(null, payTx1, cashAccount, new BigDecimal("2250.00"));
                JournalEntry payCredit1 = new JournalEntry(null, payTx1, arAccount, new BigDecimal("-2250.00"));
                payTx1.getEntries().add(payDebit1);
                payTx1.getEntries().add(payCredit1);
                transactionRepository.save(payTx1);
                payment1.setTransaction(payTx1);
                paymentRepository.save(payment1);

                // Invoice 2: POSTED (awaiting payment)
                com.balancesheet.backend.model.Invoice inv2 = new com.balancesheet.backend.model.Invoice();
                inv2.setInvoiceNumber("INV-2026-002");
                inv2.setCustomer(customers.get(1));
                inv2.setDate(LocalDate.of(2026, 2, 1));
                inv2.setDueDate(LocalDate.of(2026, 3, 3));
                inv2.setStatus(com.balancesheet.backend.model.InvoiceStatus.POSTED);
                inv2.setCurrency(company.getCurrency());
                inv2.setCompany(company);

                com.balancesheet.backend.model.InvoiceItem item2a = new com.balancesheet.backend.model.InvoiceItem();
                item2a.setDescription(products.get(1).getName());
                item2a.setQuantity(new BigDecimal("30"));
                item2a.setUnitPrice(products.get(1).getSellingPrice());
                item2a.setAmount(new BigDecimal("1500.00"));
                item2a.setRevenueAccount(beerSalesAccount);
                item2a.setProduct(products.get(1));
                item2a.setInvoice(inv2);

                com.balancesheet.backend.model.InvoiceItem item2b = new com.balancesheet.backend.model.InvoiceItem();
                item2b.setDescription(products.get(2).getName());
                item2b.setQuantity(new BigDecimal("40"));
                item2b.setUnitPrice(products.get(2).getSellingPrice());
                item2b.setAmount(new BigDecimal("1600.00"));
                item2b.setRevenueAccount(beerSalesAccount);
                item2b.setProduct(products.get(2));
                item2b.setInvoice(inv2);

                inv2.getItems().add(item2a);
                inv2.getItems().add(item2b);
                inv2.setTotalAmount(new BigDecimal("3100.00"));

                Transaction tx2 = new Transaction();
                tx2.setDate(LocalDate.of(2026, 2, 1).atStartOfDay());
                tx2.setDescription("Invoice #" + inv2.getInvoiceNumber());
                tx2.setCompany(company);
                tx2.setCurrency(company.getCurrency());
                JournalEntry debit2 = new JournalEntry(null, tx2, arAccount, new BigDecimal("3100.00"));
                JournalEntry credit2 = new JournalEntry(null, tx2, beerSalesAccount, new BigDecimal("-3100.00"));
                tx2.getEntries().add(debit2);
                tx2.getEntries().add(credit2);
                transactionRepository.save(tx2);
                inv2.setTransaction(tx2);
                invoiceRepository.save(inv2);

                // Invoice 3: DRAFT
                com.balancesheet.backend.model.Invoice inv3 = new com.balancesheet.backend.model.Invoice();
                inv3.setInvoiceNumber("INV-2026-003");
                inv3.setCustomer(customers.get(2));
                inv3.setDate(LocalDate.now());
                inv3.setDueDate(LocalDate.now().plusDays(30));
                inv3.setStatus(com.balancesheet.backend.model.InvoiceStatus.DRAFT);
                inv3.setCurrency(company.getCurrency());
                inv3.setCompany(company);

                com.balancesheet.backend.model.InvoiceItem item3 = new com.balancesheet.backend.model.InvoiceItem();
                item3.setDescription(products.get(3).getName());
                item3.setQuantity(new BigDecimal("60"));
                item3.setUnitPrice(products.get(3).getSellingPrice());
                item3.setAmount(new BigDecimal("2520.00"));
                item3.setRevenueAccount(beerSalesAccount);
                item3.setProduct(products.get(3));
                item3.setInvoice(inv3);
                inv3.getItems().add(item3);
                inv3.setTotalAmount(new BigDecimal("2520.00"));
                invoiceRepository.save(inv3);

                // Invoice 4: POSTED (Multi-item, recent)
                com.balancesheet.backend.model.Invoice inv4 = new com.balancesheet.backend.model.Invoice();
                inv4.setInvoiceNumber("INV-2026-004");
                inv4.setCustomer(customers.get(3)); // Mountain View Tavern
                inv4.setDate(LocalDate.of(2026, 2, 3));
                inv4.setDueDate(LocalDate.of(2026, 3, 5));
                inv4.setStatus(com.balancesheet.backend.model.InvoiceStatus.POSTED);
                inv4.setCurrency(company.getCurrency());
                inv4.setCompany(company);

                com.balancesheet.backend.model.InvoiceItem item4a = new com.balancesheet.backend.model.InvoiceItem();
                item4a.setDescription(products.get(0).getName()); // Hoppy IPA
                item4a.setQuantity(new BigDecimal("25"));
                item4a.setUnitPrice(products.get(0).getSellingPrice());
                item4a.setAmount(new BigDecimal("1125.00"));
                item4a.setRevenueAccount(beerSalesAccount);
                item4a.setProduct(products.get(0));
                item4a.setInvoice(inv4);
                inv4.getItems().add(item4a);

                com.balancesheet.backend.model.InvoiceItem item4b = new com.balancesheet.backend.model.InvoiceItem();
                item4b.setDescription(products.get(4).getName()); // Pale Ale
                item4b.setQuantity(new BigDecimal("20"));
                item4b.setUnitPrice(products.get(4).getSellingPrice());
                item4b.setAmount(new BigDecimal("860.00"));
                item4b.setRevenueAccount(beerSalesAccount);
                item4b.setProduct(products.get(4));
                item4b.setInvoice(inv4);
                inv4.getItems().add(item4b);

                inv4.setTotalAmount(new BigDecimal("1985.00"));

                Transaction tx4 = new Transaction();
                tx4.setDate(LocalDate.of(2026, 2, 3).atStartOfDay());
                tx4.setDescription("Invoice #" + inv4.getInvoiceNumber());
                tx4.setCompany(company);
                tx4.setCurrency(company.getCurrency());
                JournalEntry debit4 = new JournalEntry(null, tx4, arAccount, new BigDecimal("1985.00"));
                JournalEntry credit4 = new JournalEntry(null, tx4, beerSalesAccount, new BigDecimal("-1985.00"));
                tx4.getEntries().add(debit4);
                tx4.getEntries().add(credit4);
                transactionRepository.save(tx4);
                inv4.setTransaction(tx4);
                invoiceRepository.save(inv4);

                // Invoice 5: PAID (Pacific Northwest Beverages - large order)
                com.balancesheet.backend.model.Invoice inv5 = new com.balancesheet.backend.model.Invoice();
                inv5.setInvoiceNumber("INV-2026-005");
                inv5.setCustomer(customers.get(4)); // Pacific Northwest Beverages
                inv5.setDate(LocalDate.of(2026, 1, 20));
                inv5.setDueDate(LocalDate.of(2026, 2, 19));
                inv5.setStatus(com.balancesheet.backend.model.InvoiceStatus.PAID);
                inv5.setCurrency(company.getCurrency());
                inv5.setCompany(company);

                com.balancesheet.backend.model.InvoiceItem item5a = new com.balancesheet.backend.model.InvoiceItem();
                item5a.setDescription(products.get(2).getName()); // Summer Wheat
                item5a.setQuantity(new BigDecimal("100"));
                item5a.setUnitPrice(products.get(2).getSellingPrice());
                item5a.setAmount(new BigDecimal("4000.00"));
                item5a.setRevenueAccount(beerSalesAccount);
                item5a.setProduct(products.get(2));
                item5a.setInvoice(inv5);
                inv5.getItems().add(item5a);

                com.balancesheet.backend.model.InvoiceItem item5b = new com.balancesheet.backend.model.InvoiceItem();
                item5b.setDescription(products.get(5).getName()); // Porter
                item5b.setQuantity(new BigDecimal("40"));
                item5b.setUnitPrice(products.get(5).getSellingPrice());
                item5b.setAmount(new BigDecimal("1920.00"));
                item5b.setRevenueAccount(beerSalesAccount);
                item5b.setProduct(products.get(5));
                item5b.setInvoice(inv5);
                inv5.getItems().add(item5b);

                inv5.setTotalAmount(new BigDecimal("5920.00"));

                Transaction tx5 = new Transaction();
                tx5.setDate(LocalDate.of(2026, 1, 20).atStartOfDay());
                tx5.setDescription("Invoice #" + inv5.getInvoiceNumber());
                tx5.setCompany(company);
                tx5.setCurrency(company.getCurrency());
                JournalEntry debit5 = new JournalEntry(null, tx5, arAccount, new BigDecimal("5920.00"));
                JournalEntry credit5 = new JournalEntry(null, tx5, beerSalesAccount, new BigDecimal("-5920.00"));
                tx5.getEntries().add(debit5);
                tx5.getEntries().add(credit5);
                transactionRepository.save(tx5);
                inv5.setTransaction(tx5);
                invoiceRepository.save(inv5);

                // Payment for invoice 5
                com.balancesheet.backend.model.Payment payment5 = new com.balancesheet.backend.model.Payment();
                payment5.setInvoice(inv5);
                payment5.setAmount(new BigDecimal("5920.00"));
                payment5.setPaymentDate(LocalDate.of(2026, 2, 1));
                payment5.setPaymentMethod("Check");
                payment5.setReference("CHK-8472");
                payment5.setCashAccount(cashAccount);
                payment5.setCompany(company);

                Transaction payTx5 = new Transaction();
                payTx5.setDate(LocalDate.of(2026, 2, 1).atStartOfDay());
                payTx5.setDescription("Payment for Invoice #" + inv5.getInvoiceNumber());
                payTx5.setCompany(company);
                payTx5.setCurrency(company.getCurrency());
                JournalEntry payDebit5 = new JournalEntry(null, payTx5, cashAccount, new BigDecimal("5920.00"));
                JournalEntry payCredit5 = new JournalEntry(null, payTx5, arAccount, new BigDecimal("-5920.00"));
                payTx5.getEntries().add(payDebit5);
                payTx5.getEntries().add(payCredit5);
                transactionRepository.save(payTx5);
                payment5.setTransaction(payTx5);
                paymentRepository.save(payment5);

                // Invoice 6: DRAFT (Brewery Direct Wholesale)
                com.balancesheet.backend.model.Invoice inv6 = new com.balancesheet.backend.model.Invoice();
                inv6.setInvoiceNumber("INV-2026-006");
                inv6.setCustomer(customers.get(5)); // Brewery Direct Wholesale
                inv6.setDate(LocalDate.now());
                inv6.setDueDate(LocalDate.now().plusDays(30));
                inv6.setStatus(com.balancesheet.backend.model.InvoiceStatus.DRAFT);
                inv6.setCurrency(company.getCurrency());
                inv6.setCompany(company);

                com.balancesheet.backend.model.InvoiceItem item6 = new com.balancesheet.backend.model.InvoiceItem();
                item6.setDescription(products.get(1).getName()); // Dark Stout
                item6.setQuantity(new BigDecimal("75"));
                item6.setUnitPrice(products.get(1).getSellingPrice());
                item6.setAmount(new BigDecimal("3750.00"));
                item6.setRevenueAccount(beerSalesAccount);
                item6.setProduct(products.get(1));
                item6.setInvoice(inv6);
                inv6.getItems().add(item6);
                inv6.setTotalAmount(new BigDecimal("3750.00"));
                invoiceRepository.save(inv6);
        }

        private void seedDemoTemplates(Company company, Map<String, Account> accounts) {
                // Template 1: Utility Payment
                com.balancesheet.backend.model.TransactionTemplate t1 = new com.balancesheet.backend.model.TransactionTemplate();
                t1.setName("Monthly Utilities");
                t1.setDescription("Electric, Water, Gas");
                t1.setCompany(company);

                com.balancesheet.backend.model.TransactionTemplateEntry e1a = new com.balancesheet.backend.model.TransactionTemplateEntry();
                e1a.setAccount(accounts.get("Utilities Expense"));
                e1a.setType("DEBIT");
                e1a.setTemplate(t1);

                com.balancesheet.backend.model.TransactionTemplateEntry e1b = new com.balancesheet.backend.model.TransactionTemplateEntry();
                e1b.setAccount(accounts.get("Cash"));
                e1b.setType("CREDIT");
                e1b.setTemplate(t1);

                t1.getEntries().add(e1a);
                t1.getEntries().add(e1b);
                templateRepository.save(t1);

                // Template 2: Insurance Payment
                com.balancesheet.backend.model.TransactionTemplate t2 = new com.balancesheet.backend.model.TransactionTemplate();
                t2.setName("Insurance Premium");
                t2.setDescription("Monthly business insurance");
                t2.setCompany(company);

                com.balancesheet.backend.model.TransactionTemplateEntry e2a = new com.balancesheet.backend.model.TransactionTemplateEntry();
                e2a.setAccount(accounts.get("Insurance Expense"));
                e2a.setType("DEBIT");
                e2a.setTemplate(t2);

                com.balancesheet.backend.model.TransactionTemplateEntry e2b = new com.balancesheet.backend.model.TransactionTemplateEntry();
                e2b.setAccount(accounts.get("Cash"));
                e2b.setType("CREDIT");
                e2b.setTemplate(t2);

                t2.getEntries().add(e2a);
                t2.getEntries().add(e2b);
                templateRepository.save(t2);

                // Template 3: Marketing Campaign
                com.balancesheet.backend.model.TransactionTemplate t3 = new com.balancesheet.backend.model.TransactionTemplate();
                t3.setName("Marketing Campaign");
                t3.setDescription("Social media & advertising");
                t3.setCompany(company);

                com.balancesheet.backend.model.TransactionTemplateEntry e3a = new com.balancesheet.backend.model.TransactionTemplateEntry();
                e3a.setAccount(accounts.get("Marketing Expense"));
                e3a.setType("DEBIT");
                e3a.setTemplate(t3);

                com.balancesheet.backend.model.TransactionTemplateEntry e3b = new com.balancesheet.backend.model.TransactionTemplateEntry();
                e3b.setAccount(accounts.get("Cash"));
                e3b.setType("CREDIT");
                e3b.setTemplate(t3);

                t3.getEntries().add(e3a);
                t3.getEntries().add(e3b);
                templateRepository.save(t3);
        }
}

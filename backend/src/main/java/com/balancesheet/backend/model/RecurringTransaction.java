package com.balancesheet.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecurringTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g., "John Doe", "HQ Building"

    @Column(nullable = false)
    private String description; // Template description

    @Column(nullable = false)
    private BigDecimal amount;

    // Accounts to Debit/Credit automatically
    @ManyToOne
    @JoinColumn(name = "debit_account_id", nullable = false)
    private Account debitAccount;

    @ManyToOne
    @JoinColumn(name = "credit_account_id", nullable = false)
    private Account creditAccount;

    // Scheduling
    @Column(nullable = false)
    private int dayOfMonth; // 1-31

    @Column(nullable = false)
    private LocalDate nextRunDate;

    // Contract / Employment Dates
    private LocalDate startDate;
    private LocalDate endDate;

    // Helper to identify if it's a "Team Member" or "Building"
    private String category; // "TEAM", "BUILDING", "OTHER"

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}

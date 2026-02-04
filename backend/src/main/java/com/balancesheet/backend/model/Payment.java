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
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate paymentDate;

    private String paymentMethod; // "Cash", "Check", "Bank Transfer", etc.

    private String reference; // Check number, transfer reference, etc.

    @ManyToOne(optional = false)
    @JoinColumn(name = "cash_account_id", nullable = false)
    private Account cashAccount; // The cash/bank account receiving the payment

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // Link to the Journal Transaction created for this payment
    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;
}

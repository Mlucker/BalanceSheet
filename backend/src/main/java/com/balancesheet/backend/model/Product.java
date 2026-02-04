package com.balancesheet.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String sku; // Stock Keeping Unit
    private String description;

    @Column(nullable = false)
    private BigDecimal sellingPrice;

    private BigDecimal costPrice;

    @Column(nullable = false)
    private int quantityOnHand = 0;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}

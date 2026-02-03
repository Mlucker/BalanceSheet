package com.balancesheet.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionTemplateEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    @JsonIgnore
    private TransactionTemplate template;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    private String description; // Default description for this line item (e.g. "VAT 20%")

    // Hint for UI: "DEBIT" or "CREDIT".
    // This helps the UI know which column to put the focus in or pre-fill.
    // It is not strictly enforced by the backend logic itself, but useful for the
    // template.
    private String type;
}

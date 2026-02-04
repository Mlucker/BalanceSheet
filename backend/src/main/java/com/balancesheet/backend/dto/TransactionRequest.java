package com.balancesheet.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class TransactionRequest {
    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Entries are required")
    @Valid
    private List<JournalEntryRequest> entries;

    private java.time.LocalDateTime date;
    private String currency; // currency code
}

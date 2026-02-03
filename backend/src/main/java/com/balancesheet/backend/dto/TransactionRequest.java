package com.balancesheet.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class TransactionRequest {
    private String description;
    private List<JournalEntryRequest> entries;
    private java.time.LocalDateTime date;
    private String currency;
}

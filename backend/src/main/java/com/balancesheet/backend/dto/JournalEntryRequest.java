package com.balancesheet.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class JournalEntryRequest {
    private Long accountId;
    private BigDecimal amount;
}

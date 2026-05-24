package com.portfolio.portfolio_tracker.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TransactionResponseDTO {
    private Long id;
    private String actionId; // "BUY" or "SELL"
    private LocalDate date;
    private BigDecimal quantity;
    private BigDecimal price;
}
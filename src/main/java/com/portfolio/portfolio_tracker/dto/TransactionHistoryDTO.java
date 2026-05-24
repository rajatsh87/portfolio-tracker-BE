package com.portfolio.portfolio_tracker.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TransactionHistoryDTO {
    private Long id;
    private String assetName;
    private String ticker;
    private String actionType; // BUY, SELL, DIVIDEND
    private LocalDate transactionDate;
    private BigDecimal quantity;
    private BigDecimal pricePerUnit;
    private BigDecimal totalValue;
}
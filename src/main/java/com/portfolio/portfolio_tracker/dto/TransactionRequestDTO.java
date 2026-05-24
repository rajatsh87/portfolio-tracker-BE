package com.portfolio.portfolio_tracker.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequestDTO {
    private Long accountId;

    // Market Asset Fields
    private String segment;
    private String actionId; // "BUY" or "SELL"
    private String currency;
    private LocalDate date;
    private String ticker;
    private BigDecimal price;
    private BigDecimal quantity;

    // Fixed Deposit Fields (Handled by a different service later, but included in the payload)
    private String bankName;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private LocalDate maturityDate;
    private BigDecimal maturityAmount;
    private String fdNumber;
}
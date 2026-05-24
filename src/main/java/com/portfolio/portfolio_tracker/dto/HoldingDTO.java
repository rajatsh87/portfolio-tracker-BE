package com.portfolio.portfolio_tracker.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class HoldingDTO {
    private Long id; // Asset ID or FD ID
    private String segment;
    private String currency;

    // Market Asset Fields (Equity, MF, Foreign)
    private String ticker;
    private String name;
    private BigDecimal avgBuyPrice;
    private BigDecimal currentPrice;
    private BigDecimal quantity;
    private BigDecimal daysChange;
    private BigDecimal daysChangePct;
    private BigDecimal profitLoss;
    private BigDecimal profitLossPct;
    private BigDecimal investedAmt;
    private BigDecimal currentVal;
    private LocalDate investmentDate;

    // Fixed Deposit Fields
    private String bankName;
    private String accountNumber;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private String maturityDate;
    private BigDecimal maturityAmount;
    private Long daysRemaining;
    private String fdNumber;
}
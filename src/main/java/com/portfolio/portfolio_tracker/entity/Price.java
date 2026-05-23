package com.portfolio.portfolio_tracker.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "prices", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ticker", "trade_date", "exchange"})
})
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ticker;

    @Column(nullable = true)
    private String exchange;

    @Column(name = "trade_date", nullable = false)
    private LocalDate tradeDate;

    @Column(name = "open_price", precision = 10, scale = 4)
    private BigDecimal openPrice;

    @Column(name = "high_price", precision = 10, scale = 4)
    private BigDecimal highPrice;

    @Column(name = "low_price", precision = 10, scale = 4)
    private BigDecimal lowPrice;

    @Column(name = "close_price", nullable = false, precision = 10, scale = 4)
    private BigDecimal closePrice;

    @Column(name = "previous_close", nullable = false, precision = 10, scale = 4)
    private BigDecimal previousClose;

    @Column(name = "price_change", nullable = false, precision = 10, scale = 4)
    private BigDecimal priceChange;

    @Column(name = "change_percent", nullable = false, precision = 10, scale = 4)
    private BigDecimal changePercentage;

    private Long volume;

    @Column(name = "fetched_at")
    private LocalDateTime fetchedAt;
}
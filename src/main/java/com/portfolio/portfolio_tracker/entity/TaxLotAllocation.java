package com.portfolio.portfolio_tracker.entity;

import com.portfolio.portfolio_tracker.entity.enums.TaxClassification;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tax_lot_allocations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaxLotAllocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sell_transaction_id", nullable = false)
    private Transaction sellTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buy_transaction_id", nullable = false)
    private Transaction buyTransaction;

    @Column(name = "quantity_allocated", precision = 15, scale = 4, nullable = false)
    private BigDecimal quantityAllocated;

    @Column(name = "realized_pnl", precision = 15, scale = 4, nullable = false)
    private BigDecimal realizedPnl;

    @Column(name = "holding_period_days", nullable = false)
    private Integer holdingPeriodDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "tax_classification", nullable = false)
    private TaxClassification taxClassification;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
package com.portfolio.portfolio_tracker.entity;

import com.portfolio.portfolio_tracker.entity.enums.FDStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fixed_deposits")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FixedDeposit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "account_number", length = 100)
    private String accountNumber;

    @Column(name = "principal_amount", precision = 15, scale = 4, nullable = false)
    private BigDecimal principalAmount;

    @Column(name = "interest_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal interestRate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "maturity_date", nullable = false)
    private LocalDate maturityDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FDStatus status = FDStatus.ACTIVE;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
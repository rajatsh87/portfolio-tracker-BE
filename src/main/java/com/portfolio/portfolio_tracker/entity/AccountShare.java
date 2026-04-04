package com.portfolio.portfolio_tracker.entity;

import com.portfolio.portfolio_tracker.entity.enums.PermissionLevel;
import com.portfolio.portfolio_tracker.entity.enums.ShareStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_shares")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class AccountShare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_by_user_id", nullable = false)
    private User sharedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_with_user_id", nullable = false)
    private User sharedWith;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_level", nullable = false)
    private PermissionLevel permissionLevel = PermissionLevel.VIEWER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShareStatus status = ShareStatus.PENDING;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
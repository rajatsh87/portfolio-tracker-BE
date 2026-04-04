package com.portfolio.portfolio_tracker.repository;

import com.portfolio.portfolio_tracker.entity.AccountShare;
import com.portfolio.portfolio_tracker.entity.enums.ShareStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountShareRepository extends JpaRepository<AccountShare, Long> {
    // Find all accounts shared WITH a specific user (Portfolio Sharing feature)
    List<AccountShare> findBySharedWithIdAndStatus(Long userId, ShareStatus status);

    // Find all shares an owner has sent out
    List<AccountShare> findBySharedById(Long userId);
}
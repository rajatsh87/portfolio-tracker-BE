package com.portfolio.portfolio_tracker.repository;

import com.portfolio.portfolio_tracker.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // Supports the multi-account requirement: find all accounts owned by a user
    List<Account> findByOwnerId(Long userId);

    // Find only active accounts
    List<Account> findByOwnerIdAndIsActiveTrue(Long userId);
}
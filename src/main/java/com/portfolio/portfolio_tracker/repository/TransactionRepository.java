package com.portfolio.portfolio_tracker.repository;

import com.portfolio.portfolio_tracker.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Fetch all transactions for a specific account
    List<Transaction> findByAccountId(Long accountId);

    // Fetch transactions for a specific asset within an account (used for Avg Buy Price calculation)
    List<Transaction> findByAccountIdAndAssetIdOrderByTransactionDateAsc(Long accountId, Long assetId);
    List<Transaction> findByAccountIdAndAssetTickerOrderByTransactionDateDesc(Long accountId, String ticker);
}
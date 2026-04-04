package com.portfolio.portfolio_tracker.repository;

import com.portfolio.portfolio_tracker.entity.TaxLotAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxLotAllocationRepository extends JpaRepository<TaxLotAllocation, Long> {
    // Find all tax lot matching records for a specific SELL transaction
    List<TaxLotAllocation> findBySellTransactionId(Long sellTransactionId);
}
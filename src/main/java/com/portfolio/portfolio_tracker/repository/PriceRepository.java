package com.portfolio.portfolio_tracker.repository;


import com.portfolio.portfolio_tracker.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
    @Query("SELECT p FROM Price p " +
            "WHERE p.ticker IN :tickers " +
            "AND p.fetchedAt = (SELECT MAX(p2.fetchedAt) FROM Price p2 WHERE p2.ticker = p.ticker)")
    List<Price> findLatestPricesForTickers(@Param("tickers") Set<String> tickers);
}

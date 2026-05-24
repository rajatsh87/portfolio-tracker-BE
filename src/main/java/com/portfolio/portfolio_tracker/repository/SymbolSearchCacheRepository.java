package com.portfolio.portfolio_tracker.repository;

import com.portfolio.portfolio_tracker.entity.SymbolSearchCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SymbolSearchCacheRepository extends JpaRepository<SymbolSearchCache, Long> {
    Optional<SymbolSearchCache> findByKeyword(String keyword);
}
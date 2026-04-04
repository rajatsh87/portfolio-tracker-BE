package com.portfolio.portfolio_tracker.repository;

import com.portfolio.portfolio_tracker.entity.AssetCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetCatalogRepository extends JpaRepository<AssetCatalog, Long> {
    Optional<AssetCatalog> findByTicker(String ticker);
}
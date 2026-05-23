package com.portfolio.portfolio_tracker.repository;

import com.portfolio.portfolio_tracker.entity.AssetCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetCatalogRepository extends JpaRepository<AssetCatalog, Long> {

    // Using raw SQL instead of JPQL
    @Query(value = "SELECT * FROM assets_catalog WHERE segment = :segment AND ticker LIKE CONCAT('%', :query, '%')", nativeQuery = true)
    List<AssetCatalog> findAssetsWithRawSql(@Param("query") String query, @Param("segment") String segment);

    Optional<AssetCatalog> findByTicker(String ticker);
}
package com.portfolio.portfolio_tracker.service;

import com.portfolio.portfolio_tracker.dto.AssetDTO;
import com.portfolio.portfolio_tracker.entity.AssetCatalog;
import com.portfolio.portfolio_tracker.repository.AssetCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetCatalogRepository assetCatalogRepository;

    public List<AssetDTO> searchAssets(String query) {
        // In a real app, you'd use a LIKE query in the repository: findByTickerContainingIgnoreCaseOrNameContainingIgnoreCase
        // For now, we fetch all and filter in memory as a placeholder
        return assetCatalogRepository.findAll().stream()
                .filter(asset -> asset.getTicker().toLowerCase().contains(query.toLowerCase()) ||
                        asset.getName().toLowerCase().contains(query.toLowerCase()))
                .map(asset -> AssetDTO.builder()
                        .id(asset.getId())
                        .ticker(asset.getTicker())
                        .name(asset.getName())
                        .segment(asset.getSegment().name())
                        .currency(asset.getCurrency())
                        .build())
                .collect(Collectors.toList());
    }
}
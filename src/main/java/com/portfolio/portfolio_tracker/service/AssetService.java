package com.portfolio.portfolio_tracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.portfolio_tracker.dto.AlphaVantageResponse;
import com.portfolio.portfolio_tracker.dto.AssetDTO;
import com.portfolio.portfolio_tracker.entity.AssetCatalog;
import com.portfolio.portfolio_tracker.entity.enums.Segment;
import com.portfolio.portfolio_tracker.repository.AssetCatalogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetService {

    private final AssetCatalogRepository assetCatalogRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${alphavantage.api.key:DEMO}")
    private String apiKey;

    public List<AssetDTO> searchAssets(String query, String segment) {
        List<AssetCatalog> localResults = assetCatalogRepository
                .findAssetsWithRawSql(query, segment);

        if (!localResults.isEmpty()) {
            log.info("Found {} local results for query '{}'", localResults.size(), query);
            return mapToDTOList(localResults);
        }

        // 2. If nothing found locally, fetch from Alpha Vantage
        log.info("No local results found. Fetching '{}' from Alpha Vantage...", query);
        String url = String.format("https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=%s&apikey=%s", query, apiKey);

        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            AlphaVantageResponse response = objectMapper.readValue(jsonResponse, AlphaVantageResponse.class);

            if (response.getBestMatches() == null || response.getBestMatches().isEmpty()) {
                return new ArrayList<>();
            }

            List<AssetCatalog> newlySavedAssets = new ArrayList<>();

            // 3. Process each match and save to the catalog if it doesn't exist
            for (AlphaVantageResponse.Match match : response.getBestMatches()) {
                Optional<AssetCatalog> existingAsset = assetCatalogRepository.findByTicker(match.getSymbol());

                if (existingAsset.isEmpty()) {
                    String[] tickerExchange = match.getSymbol().split("\\.");
                    AssetCatalog newAsset = AssetCatalog.builder()
                            .ticker(tickerExchange[0])
                            .exchange(tickerExchange.length > 1 ? tickerExchange[1] : null)
                            .name(match.getName())
                            .segment(mapApiTypeToSegment(match.getCurrency()))
                            .currency(match.getCurrency() != null ? match.getCurrency() : "INR")
                            .source("Alpha Vantage")
                            .region(match.getRegion())
                            .build();

                    assetCatalogRepository.save(newAsset);
                    newlySavedAssets.add(newAsset);
                } else {
                    newlySavedAssets.add(existingAsset.get());
                }
            }

            return mapToDTOList(newlySavedAssets);

        } catch (Exception e) {
            log.error("Failed to fetch or process data from Alpha Vantage", e);
            throw new RuntimeException("External API call failed");
        }
    }

    // Helper to map Alpha Vantage "type" to our Segment Enum safely
    private Segment mapApiTypeToSegment(String currency) {
        if (currency == null) return null;
        String typeLower = currency.toLowerCase();

        if (!"inr".equals(typeLower)){
            return Segment.FOREIGN_EQUITY;
        }

        if (typeLower.contains("mutual fund")) {
            return Segment.MUTUAL_FUND;
        }
        if (typeLower.contains("etf")){
            return Segment.ETF;
        }
        return Segment.EQUITY;
    }

    // Helper to map Entity to DTO
    private List<AssetDTO> mapToDTOList(List<AssetCatalog> assets) {
        return assets.stream()
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
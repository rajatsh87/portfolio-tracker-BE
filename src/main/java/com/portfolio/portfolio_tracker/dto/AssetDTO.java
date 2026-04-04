package com.portfolio.portfolio_tracker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssetDTO {
    private Long id;
    private String ticker;
    private String name;
    private String segment;
    private String currency;
}
package com.portfolio.portfolio_tracker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDTO {
    private Long id;
    private String name;
    private Boolean isActive;
}
package com.portfolio.portfolio_tracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class AlphaVantageResponse {
    @JsonProperty("bestMatches")
    private List<Match> bestMatches;

    @Data
    public static class Match {
        @JsonProperty("1. symbol") private String symbol;
        @JsonProperty("2. name") private String name;
        @JsonProperty("3. type") private String type;
        @JsonProperty("4. region") private String region;
        @JsonProperty("8. currency") private String currency;
        @JsonProperty("9. matchScore") private String matchScore;
    }
}
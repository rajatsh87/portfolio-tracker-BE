package com.portfolio.portfolio_tracker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "symbol_search_cache")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SymbolSearchCache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String keyword;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String jsonResponse;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
}
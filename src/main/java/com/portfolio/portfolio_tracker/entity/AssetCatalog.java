package com.portfolio.portfolio_tracker.entity;

import com.portfolio.portfolio_tracker.entity.enums.Segment;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assets_catalog")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssetCatalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ticker;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Segment segment;

    @Column(nullable = false, length = 3)
    private String currency = "INR";
}
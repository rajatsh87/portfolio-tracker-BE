package com.portfolio.portfolio_tracker.controller;

import com.portfolio.portfolio_tracker.dto.AssetDTO;
import com.portfolio.portfolio_tracker.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AssetController {

    private final AssetService assetService;

    @GetMapping("/search")
    public ResponseEntity<List<AssetDTO>> searchAssets(@RequestParam String query, @RequestParam String segment) {
        return ResponseEntity.ok(assetService.searchAssets(query, segment));
    }
}
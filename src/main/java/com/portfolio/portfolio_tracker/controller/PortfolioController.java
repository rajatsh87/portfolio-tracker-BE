package com.portfolio.portfolio_tracker.controller;

import com.portfolio.portfolio_tracker.dto.HoldingDTO;
import com.portfolio.portfolio_tracker.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // Allows Vue to talk to Spring Boot
public class PortfolioController {

    @Autowired
    PortfolioService portfolioService;

    @GetMapping("/{accountId}/holdings")
    public ResponseEntity<List<HoldingDTO>> getHoldings(@PathVariable Long accountId) {
        // Fetches all holdings, processes the rolling average, and returns the unified DTOs
        List<HoldingDTO> holdings = portfolioService.getPortfolioHoldings(accountId);
        return ResponseEntity.ok(holdings);
    }
}
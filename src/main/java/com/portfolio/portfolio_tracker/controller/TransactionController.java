package com.portfolio.portfolio_tracker.controller;

import com.portfolio.portfolio_tracker.dto.TransactionHistoryDTO;
import com.portfolio.portfolio_tracker.dto.TransactionRequestDTO;
import com.portfolio.portfolio_tracker.dto.TransactionResponseDTO;
import com.portfolio.portfolio_tracker.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<String> addTransaction(@RequestBody TransactionRequestDTO request) {
        if ("fds".equalsIgnoreCase(request.getSegment())) {
            transactionService.processFixedDeposit(request);
        } else {
            transactionService.processMarketTransaction(request);
        }
        return ResponseEntity.ok("Transaction saved successfully");
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionHistoryDTO>> getTransactionHistory(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getTransactionHistory(accountId));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getTransactions(
            @RequestParam Long accountId,
            @RequestParam String ticker) {

        List<TransactionResponseDTO> history = transactionService.getTransactionsByTicker(accountId, ticker);
        return ResponseEntity.ok(history);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok("Transaction deleted successfully");
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> updateTransaction(@PathVariable Long id, @RequestBody TransactionRequestDTO request) {
        transactionService.updateTransaction(id, request);
        return ResponseEntity.ok("Transaction updated successfully");
    }
}
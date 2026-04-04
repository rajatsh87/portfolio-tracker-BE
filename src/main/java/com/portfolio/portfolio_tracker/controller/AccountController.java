package com.portfolio.portfolio_tracker.controller;

import com.portfolio.portfolio_tracker.dto.AccountDTO;
import com.portfolio.portfolio_tracker.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountDTO>> getUserAccounts(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getUserAccounts(userId));
    }
}
package com.portfolio.portfolio_tracker.service;

import com.portfolio.portfolio_tracker.dto.AccountDTO;
import com.portfolio.portfolio_tracker.entity.Account;
import com.portfolio.portfolio_tracker.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public List<AccountDTO> getUserAccounts(Long userId) {
        List<Account> accounts = accountRepository.findByOwnerIdAndIsActiveTrue(userId);

        return accounts.stream()
                .map(acc -> AccountDTO.builder()
                        .id(acc.getId())
                        .name(acc.getName())
                        .isActive(acc.getIsActive())
                        .build())
                .collect(Collectors.toList());
    }
}
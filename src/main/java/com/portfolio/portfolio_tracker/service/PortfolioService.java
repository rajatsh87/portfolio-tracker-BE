package com.portfolio.portfolio_tracker.service;

import com.portfolio.portfolio_tracker.dto.HoldingDTO;
import com.portfolio.portfolio_tracker.entity.AssetCatalog;
import com.portfolio.portfolio_tracker.entity.FixedDeposit;
import com.portfolio.portfolio_tracker.entity.Price;
import com.portfolio.portfolio_tracker.entity.Transaction;
import com.portfolio.portfolio_tracker.entity.enums.ActionType;
import com.portfolio.portfolio_tracker.repository.FixedDepositRepository;
import com.portfolio.portfolio_tracker.repository.PriceRepository;
import com.portfolio.portfolio_tracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final TransactionRepository transactionRepository;
    private final FixedDepositRepository fixedDepositRepository;
    private final PriceRepository priceRepository;

    public List<HoldingDTO> getPortfolioHoldings(Long accountId) {
        List<HoldingDTO> portfolio = new ArrayList<>();

        // 1. Process Market Assets (Equities, MFs) from the Ledger
        List<Transaction> allTransactions = transactionRepository.findByAccountId(accountId);

        // Group transactions by Asset
        Map<AssetCatalog, List<Transaction>> transactionsByAsset = allTransactions.stream()
                .collect(Collectors.groupingBy(Transaction::getAsset));

        Set<String> allSymbols = transactionsByAsset.keySet().stream().map(AssetCatalog::getTicker).collect(Collectors.toSet());
        List<Price> latestPricesForTickers = priceRepository.findLatestPricesForTickers(allSymbols);
        Map<String, Price> priceMap = latestPricesForTickers.stream().collect(Collectors.toMap(Price::getTicker, price -> price));

        for (Map.Entry<AssetCatalog, List<Transaction>> entry : transactionsByAsset.entrySet()) {
            AssetCatalog asset = entry.getKey();
            List<Transaction> txns = entry.getValue();

            // Sort chronologically to calculate accurate rolling average
            txns.sort((t1, t2) -> t1.getTransactionDate().compareTo(t2.getTransactionDate()));

            BigDecimal currentQuantity = BigDecimal.ZERO;
            BigDecimal totalInvestedCost = BigDecimal.ZERO;
            BigDecimal profitLoss = BigDecimal.ZERO;

            for (Transaction txn : txns) {
                if (txn.getActionType() == ActionType.BUY) {
                    currentQuantity = currentQuantity.add(txn.getQuantity());
                    BigDecimal costOfPurchase = txn.getQuantity().multiply(txn.getPricePerUnit());
                    totalInvestedCost = totalInvestedCost.add(costOfPurchase);
                } else if (txn.getActionType() == ActionType.SELL) {
                    // Reduce quantity and proportionately reduce the total invested cost
                    if (currentQuantity.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal avgPriceAtSell = totalInvestedCost.divide(currentQuantity, 4, RoundingMode.HALF_UP);
                        BigDecimal costOfSharesSold = txn.getQuantity().multiply(avgPriceAtSell);

                        currentQuantity = currentQuantity.subtract(txn.getQuantity());
                        totalInvestedCost = totalInvestedCost.subtract(costOfSharesSold);
                    }
                }
            }

            // Only add to holdings if the user still owns shares (quantity > 0)
            if (currentQuantity.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal avgBuyPrice = totalInvestedCost.divide(currentQuantity, 4, RoundingMode.HALF_UP);
                BigDecimal currentPrice = priceMap.getOrDefault(asset.getTicker(), new Price()).getClosePrice();

                HoldingDTO holdingDTO = HoldingDTO.builder()
                        .id(asset.getId())
                        .segment(asset.getSegment().name().toLowerCase().replace("_", "-"))
                        .currency(asset.getCurrency())
                        .ticker(asset.getTicker())
                        .name(asset.getName())
                        .quantity(currentQuantity)
                        .avgBuyPrice(avgBuyPrice)
                        .currentPrice(currentPrice)
                        .daysChange(priceMap.get(asset.getTicker()).getPriceChange())
                        .daysChangePct(priceMap.get(asset.getTicker()).getChangePercentage())
                        .investedAmt(totalInvestedCost)
                        .build();
                populatePAndL(holdingDTO);
                portfolio.add(holdingDTO);

            }
        }

        // 2. Process Fixed Deposits (Stateful Contracts)
        List<FixedDeposit> fixedDeposits = fixedDepositRepository.findByAccountId(accountId);
        for (FixedDeposit fd : fixedDeposits) {
            long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), fd.getMaturityDate());
            daysRemaining = daysRemaining < 0 ? 0 : daysRemaining;
            portfolio.add(HoldingDTO.builder()
                    .id(fd.getId())
                    .segment("fds")
                    .currency("INR")
                    .bankName(fd.getBankName())
                    .accountNumber(fd.getAccountNumber())
                    .principalAmount(fd.getPrincipalAmount())
                    .interestRate(fd.getInterestRate())
                    .maturityDate(fd.getMaturityDate().toString())
                    .daysRemaining(daysRemaining)
                    .maturityAmount(getMaturityAmount(fd))
                    .investmentDate(fd.getStartDate())
                    .fdNumber(fd.getAccountNumber())
                    .build());
        }

        return portfolio;
    }

    void populatePAndL(HoldingDTO holdingDTO) {
        BigDecimal currentValue = holdingDTO.getCurrentPrice().multiply(holdingDTO.getQuantity());
        BigDecimal profitOrLoss = currentValue.subtract(holdingDTO.getInvestedAmt());
        BigDecimal profitOrLossPrct = profitOrLoss.divide(holdingDTO.getInvestedAmt(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        holdingDTO.setCurrentVal(currentValue);
        holdingDTO.setProfitLoss(profitOrLoss);
        holdingDTO.setProfitLossPct(profitOrLossPrct);

    }

    BigDecimal getMaturityAmount(FixedDeposit fd) {
        long totalDays = ChronoUnit.DAYS.between(fd.getStartDate(), fd.getMaturityDate());
        double years = totalDays / 365.0;
        double rateDecimal = fd.getInterestRate().doubleValue() / 100.0;
        return fd.getPrincipalAmount().multiply(
                BigDecimal.valueOf(Math.pow(1.0 + rateDecimal, years))
        ).setScale(2, RoundingMode.HALF_UP);
    }
}
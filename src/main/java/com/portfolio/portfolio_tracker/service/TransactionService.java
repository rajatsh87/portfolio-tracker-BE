package com.portfolio.portfolio_tracker.service;

import com.portfolio.portfolio_tracker.dto.TransactionHistoryDTO;
import com.portfolio.portfolio_tracker.dto.TransactionRequestDTO;
import com.portfolio.portfolio_tracker.dto.TransactionResponseDTO;
import com.portfolio.portfolio_tracker.entity.*;
import com.portfolio.portfolio_tracker.entity.enums.ActionType;
import com.portfolio.portfolio_tracker.entity.enums.Segment;
import com.portfolio.portfolio_tracker.entity.enums.TaxClassification;
import com.portfolio.portfolio_tracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AssetCatalogRepository assetCatalogRepository;
    private final TaxLotAllocationRepository taxLotAllocationRepository;
    private final FixedDepositRepository fixedDepositRepository;

    @Transactional
    public void processMarketTransaction(TransactionRequestDTO request) {
        // 1. Fetch Account
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // 2. Find or Create Asset in Catalog (Simulating a real asset search)
        AssetCatalog asset = assetCatalogRepository.findByTicker(request.getTicker())
                .orElseGet(() -> {
                    AssetCatalog newAsset = AssetCatalog.builder()
                            .ticker(request.getTicker())
                            .name(request.getTicker()) // Fallback name
                            .segment(Segment.valueOf(request.getSegment().toUpperCase().replace("-", "_")))
                            .currency(request.getCurrency())
                            .build();
                    return assetCatalogRepository.save(newAsset);
                });

        ActionType action = ActionType.valueOf(request.getActionId().toUpperCase());

        // 3. Create and Save the Transaction Ledger Entry
        Transaction transaction = Transaction.builder()
                .account(account)
                .asset(asset)
                .actionType(action)
                .transactionDate(request.getDate())
                .quantity(request.getQuantity())
                .pricePerUnit(request.getPrice())
                .build();

        transaction = transactionRepository.save(transaction);

        // 4. Trigger the FIFO Tax Lot Matching Engine if it's a SELL
        if (action == ActionType.SELL) {
            executeFifoMatching(transaction, account.getId(), asset.getId());
        }
    }

    @Transactional
    public void processFixedDeposit(TransactionRequestDTO request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        FixedDeposit fd = FixedDeposit.builder()
                .account(account)
                .bankName(request.getBankName())
                .principalAmount(request.getPrincipalAmount())
                .interestRate(request.getInterestRate())
                .startDate(request.getDate())
                .maturityDate(request.getMaturityDate())
                .status(com.portfolio.portfolio_tracker.entity.enums.FDStatus.ACTIVE)
                .build();

        fixedDepositRepository.save(fd);
    }

    /**
     * The FIFO (First-In-First-Out) Matching Engine
     */
    private void executeFifoMatching(Transaction sellTxn, Long accountId, Long assetId) {
        BigDecimal remainingQuantityToSell = sellTxn.getQuantity();

        // Get all historical BUYS for this asset, chronologically ordered
        List<Transaction> historicalBuys = transactionRepository
                .findByAccountIdAndAssetIdOrderByTransactionDateAsc(accountId, assetId)
                .stream()
                .filter(t -> t.getActionType() == ActionType.BUY)
                .toList();

        for (Transaction buyTxn : historicalBuys) {
            if (remainingQuantityToSell.compareTo(BigDecimal.ZERO) <= 0) break; // All sold shares are matched

            // Calculate how much of this specific BUY is still available
            BigDecimal alreadyAllocatedQty = taxLotAllocationRepository.findAll().stream()
                    .filter(lot -> lot.getBuyTransaction().getId().equals(buyTxn.getId()))
                    .map(TaxLotAllocation::getQuantityAllocated)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal availableQtyInThisBuy = buyTxn.getQuantity().subtract(alreadyAllocatedQty);

            if (availableQtyInThisBuy.compareTo(BigDecimal.ZERO) > 0) {
                // We can use this BUY! Take whichever is smaller: the remaining sell amount, or the available buy amount
                BigDecimal qtyToAllocate = remainingQuantityToSell.min(availableQtyInThisBuy);

                // Calculate PnL: (Sell Price - Buy Price) * Quantity
                BigDecimal priceDifference = sellTxn.getPricePerUnit().subtract(buyTxn.getPricePerUnit());
                BigDecimal realizedPnl = priceDifference.multiply(qtyToAllocate);

                // Calculate Holding Period & Tax Classification
                long holdingDays = ChronoUnit.DAYS.between(buyTxn.getTransactionDate(), sellTxn.getTransactionDate());
                // Simple rule: > 365 days is Long Term Capital Gains (LTCG)
                TaxClassification taxClass = holdingDays > 365 ? TaxClassification.LTCG : TaxClassification.STCG;

                // Save the exact match
                TaxLotAllocation lot = TaxLotAllocation.builder()
                        .sellTransaction(sellTxn)
                        .buyTransaction(buyTxn)
                        .quantityAllocated(qtyToAllocate)
                        .realizedPnl(realizedPnl)
                        .holdingPeriodDays((int) holdingDays)
                        .taxClassification(taxClass)
                        .build();

                taxLotAllocationRepository.save(lot);

                // Deduct from our running total
                remainingQuantityToSell = remainingQuantityToSell.subtract(qtyToAllocate);
            }
        }

        // Safety check: If a user tries to sell more shares than they own, we should flag it
        if (remainingQuantityToSell.compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Insufficient holding quantity to execute this sell transaction.");
        }
    }
    public List<TransactionHistoryDTO> getTransactionHistory(Long accountId) {
        return transactionRepository.findByAccountId(accountId).stream()
                .sorted((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate())) // Newest first
                .map(txn -> TransactionHistoryDTO.builder()
                        .id(txn.getId())
                        .assetName(txn.getAsset().getName())
                        .ticker(txn.getAsset().getTicker())
                        .actionType(txn.getActionType().name())
                        .transactionDate(txn.getTransactionDate())
                        .quantity(txn.getQuantity())
                        .pricePerUnit(txn.getPricePerUnit())
                        .totalValue(txn.getQuantity().multiply(txn.getPricePerUnit()))
                        .build())
                .toList();
    }
//    public List<TransactionResponseDTO> getTransactionsByTicker(Long accountId, String ticker) {
//        List<Transaction> transactions = transactionRepository
//                .findByAccountIdAndAssetTickerOrderByDateDesc(accountId, ticker);
//
//        return transactions.stream().map(tx -> TransactionResponseDTO.builder()
//                .id(tx.getId())
//                .actionId(tx.getActionId().name())
//                .date(tx.getDate())
//                .quantity(tx.getQuantity())
//                .price(tx.getPrice())
//                .build()
//        ).toList();
//    }
}
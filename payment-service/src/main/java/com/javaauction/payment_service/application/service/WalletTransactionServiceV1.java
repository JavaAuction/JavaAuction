package com.javaauction.payment_service.application.service;

import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.domain.model.Wallet;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import com.javaauction.payment_service.domain.repository.WalletRepository;
import com.javaauction.payment_service.domain.repository.WalletTransactionRepository;
import com.javaauction.payment_service.presentation.advice.PaymentException;
import com.javaauction.payment_service.presentation.dto.request.ReqSettleDto;
import com.javaauction.payment_service.presentation.dto.response.ResGetTransactionDto;
import com.javaauction.payment_service.presentation.dto.response.ResGetTransactionsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.javaauction.payment_service.domain.enums.HoldStatus.HOLD_ACTIVE;
import static com.javaauction.payment_service.domain.enums.HoldStatus.HOLD_CAPTURED;
import static com.javaauction.payment_service.domain.enums.TransactionType.HOLD;
import static com.javaauction.payment_service.domain.enums.TransactionType.SELLER_PROCEED;
import static com.javaauction.payment_service.presentation.advice.PaymentErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletTransactionServiceV1 {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final FeeCalculator feeCalculator;

    public Page<ResGetTransactionsDto> getTransactions(
            UUID walletId, Pageable pageable, List<TransactionType> transactionTypes, Long minAmount, Long maxAmount
    ) {
        Page<WalletTransaction> walletTransactions = walletTransactionRepository.findByWalletId(
                walletId, pageable, transactionTypes, minAmount, maxAmount
        );

        return walletTransactions.map(ResGetTransactionsDto::from);
    }

    public ResGetTransactionDto getTransaction(UUID walletId, UUID transactionId) {

        WalletTransaction walletTransaction = walletTransactionRepository.findById(transactionId);

        if (!walletTransaction.getWalletId().equals(walletId))
            throw new PaymentException(WALLET_TRANSACTION_INVALID_RELATION);

        return ResGetTransactionDto.fromDomain(walletTransaction);
    }

    @Transactional
    public void settle(ReqSettleDto request) {

        WalletTransaction hold = walletTransactionRepository
                .findByAuctionIdAndTransactionTypeAndHoldStatus(request.getAuctionId(), HOLD, HOLD_ACTIVE)
                .orElseThrow(() -> new PaymentException(WALLET_TRANSACTION_HOLD_NOT_FOUND));

        if (!Objects.equals(hold.getAmount(), request.getWinningPrice()))
            throw new PaymentException(WALLET_TRANSACTION_WINNING_BID_PRICE_MISMATCH);

        Wallet buyerWallet = walletRepository.findById(hold.getWalletId())
                .orElseThrow(() -> new PaymentException(WALLET_NOT_FOUND));

        if (!buyerWallet.getUserId().equals(request.getBuyerId()))
            throw new PaymentException(WALLET_BUYER_MISMATCH);

        WalletTransaction captured = hold.withHoldStatus(HOLD_CAPTURED);
        walletTransactionRepository.save(captured);

        Wallet sellerWallet = walletRepository.findByUserId(request.getSellerId())
                .orElseThrow(() -> new PaymentException(WALLET_NOT_FOUND));

        long sellerBeforeAmount = sellerWallet.getBalance();
        long sellerNetAmount = feeCalculator.calculateNetAmount(hold.getAmount());

        Wallet settled = sellerWallet.withBalance(sellerBeforeAmount + sellerNetAmount);
        walletRepository.save(settled);

        walletTransactionRepository.save(
                WalletTransaction.builder()
                        .walletId(settled.getId())
                        .amount(sellerNetAmount)
                        .transactionType(SELLER_PROCEED)
                        .auctionId(request.getAuctionId())
                        .build()
        );
    }
}

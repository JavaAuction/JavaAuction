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

import static com.javaauction.payment_service.domain.enums.HoldStatus.HOLD_CAPTURED;
import static com.javaauction.payment_service.domain.enums.TransactionType.PAYMENT;
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

        TransactionType transactionType = request.getTransactionType();

        switch (transactionType) {
            case PAYMENT -> settlePayment(request);

            case HOLD -> settleHold(request);

            default -> throw new PaymentException(WALLET_INVALID_TRANSACTION_TYPE);
        }
    }

    private void settlePayment(ReqSettleDto request) {

        WalletTransaction payment = walletTransactionRepository
                .findByAuctionIdAndTransactionType(request.getAuctionId(), PAYMENT)
                .orElseThrow(() -> new PaymentException(WALLET_TRANSACTION_PAYMENT_NOT_FOUND));

        verifyAmountAndBuyer(payment, request.getBuyerId(), request.getAmount());

        settleSellerProceeds(request.getSellerId(), payment, request.getAuctionId());
    }

    private void settleHold(ReqSettleDto request) {

        WalletTransaction hold = walletTransactionRepository
                .findActiveHoldForUpdate(request.getAuctionId())
                .orElseThrow(() -> new PaymentException(WALLET_TRANSACTION_HOLD_NOT_FOUND));

        verifyAmountAndBuyer(hold, request.getBuyerId(), request.getAmount());

        WalletTransaction captured = hold.withHoldStatus(HOLD_CAPTURED);
        walletTransactionRepository.save(captured);

        settleSellerProceeds(request.getSellerId(), hold, request.getAuctionId());
    }

    private void verifyAmountAndBuyer(WalletTransaction walletTransaction, String buyerId, Long amount) {
        if (!Objects.equals(walletTransaction.getAmount(), amount))
            throw new PaymentException(WALLET_TRANSACTION_AMOUNT_MISMATCH);

        Wallet buyerWallet = walletRepository.findByIdForUpdate(walletTransaction.getWalletId())
                .orElseThrow(() -> new PaymentException(WALLET_NOT_FOUND));

        if (!buyerWallet.getUserId().equals(buyerId))
            throw new PaymentException(WALLET_BUYER_MISMATCH);
    }

    private void settleSellerProceeds(String sellerId, WalletTransaction walletTransaction, UUID auctionId) {
        Wallet sellerWallet = walletRepository.findByUserIdForUpdate(sellerId)
                .orElseThrow(() -> new PaymentException(WALLET_NOT_FOUND));

        long sellerBeforeAmount = sellerWallet.getBalance();
        long sellerNetAmount = feeCalculator.calculateNetAmount(walletTransaction.getAmount());

        Wallet settled = sellerWallet.withBalance(sellerBeforeAmount + sellerNetAmount);
        walletRepository.save(settled);

        walletTransactionRepository.save(
                WalletTransaction.builder()
                        .walletId(settled.getId())
                        .amount(sellerNetAmount)
                        .transactionType(SELLER_PROCEED)
                        .auctionId(auctionId)
                        .build()
        );
    }
}

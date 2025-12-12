package com.javaauction.payment_service.application.service;

import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.domain.model.Wallet;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import com.javaauction.payment_service.domain.repository.WalletRepository;
import com.javaauction.payment_service.domain.repository.WalletTransactionRepository;
import com.javaauction.payment_service.presentation.advice.PaymentException;
import com.javaauction.payment_service.presentation.dto.request.*;
import com.javaauction.payment_service.presentation.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.javaauction.payment_service.domain.enums.HoldStatus.HOLD_ACTIVE;
import static com.javaauction.payment_service.domain.enums.HoldStatus.HOLD_RELEASED;
import static com.javaauction.payment_service.domain.enums.TransactionType.*;
import static com.javaauction.payment_service.presentation.advice.PaymentErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletServiceV1 {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public static final String ADMIN = "ADMIN";

    @Transactional
    public ResCreateDto create(ReqCreateDto request) {

        Wallet wallet = walletRepository.save(
                Wallet.builder()
                        .userId(request.getUserId())
                        .build()
        );

        return ResCreateDto.from(wallet);
    }

    public ResGetWallet getWalletByUserId(String userId) {
        return ResGetWallet.from(findWalletByUserId(userId));
    }

    public ResGetWallet getWalletById(UUID walletId, String role) {
        if (isNotAdmin(role))
            throw new PaymentException(WALLET_ACCESS_DENIED);

        return ResGetWallet.from(findWalletById(walletId));
    }

    @Transactional
    public ResChargeDto charge(UUID walletId, ReqChargeDto request, String userId, String role) {

        Wallet wallet = findWalletById(walletId);

        if (isNotAdmin(role) && isNotOwner(wallet, userId)) {
            throw new PaymentException(WALLET_OWNER_MISMATCH);
        }

        long beforeBalance = wallet.getBalance();
        long chargeAmount = request.getChargeAmount();

        Wallet charged = wallet.withBalance(beforeBalance + chargeAmount);
        walletRepository.save(charged);

        WalletTransaction walletTransaction = walletTransactionRepository.save(
                WalletTransaction.builder()
                        .walletId(charged.getId())
                        .transactionType(CHARGE)
                        .amount(chargeAmount)
                        .build()
        );

        return ResChargeDto.from(charged, walletTransaction, beforeBalance);
    }

    @Transactional
    public ResWithdrawDto withdraw(UUID walletId, ReqWithdrawDto request, String userId, String role) {

        Wallet wallet = findWalletById(walletId);

        if (isNotAdmin(role) && isNotOwner(wallet, userId)) {
            throw new PaymentException(WALLET_OWNER_MISMATCH);
        }

        long beforeBalance = wallet.getBalance();
        long withdrawalAmount = request.getWithdrawAmount();

        if (withdrawalAmount > beforeBalance)
            throw new PaymentException(WALLET_INSUFFICIENT_BALANCE);

        Wallet withdrew = wallet.withBalance(beforeBalance - withdrawalAmount);
        walletRepository.save(withdrew);

        WalletTransaction walletTransaction = walletTransactionRepository.save(
                WalletTransaction.builder()
                        .walletId(withdrew.getId())
                        .transactionType(WITHDRAWAL)
                        .amount(withdrawalAmount)
                        .build()
        );

        return ResWithdrawDto.from(withdrew, walletTransaction, beforeBalance);
    }

    @Transactional
    public ResDeductDto deduct(ReqDeductDto request) {

        Wallet wallet = findWalletByUserId(request.getUserId());

        return switch (request.getTransactionType()) {
            case PAYMENT -> handlePaymentDeduct(wallet, request);
            case HOLD -> handleHoldDeduct(wallet, request);
            default -> throw new PaymentException(WALLET_INVALID_TRANSACTION_TYPE);
        };
    }

    public Boolean validate(ReqValidateDto request) {
        Wallet wallet = findWalletByUserId(request.getUserId());

        return wallet.getBalance() >= request.getBidPrice();
    }

    // ====================================== 유틸 메서드 ======================================

    private Wallet findWalletById(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new PaymentException(WALLET_NOT_FOUND));
    }

    private Wallet findWalletByUserId(String userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new PaymentException(WALLET_NOT_FOUND));
    }

    private ResDeductDto handlePaymentDeduct(Wallet wallet, ReqDeductDto request) {

        long amount = request.getDeductAmount();

        validateSufficientBalance(wallet, amount);

        return applyDeductAndCreateTransaction(
                wallet,
                amount,
                PAYMENT,
                request.getAuctionId(),
                request.getBidId()
        );
    }

    private ResDeductDto handleHoldDeduct(Wallet wallet, ReqDeductDto request) {

        if (request.getBidId() == null) {
            throw new PaymentException(WALLET_MISSING_BID_ID);
        }

        long amount = request.getDeductAmount();

        validateSufficientBalance(wallet, amount);

        releasePreviousHoldIfExists(request, amount);

        return applyDeductAndCreateTransaction(
                wallet,
                amount,
                HOLD,
                request.getAuctionId(),
                request.getBidId()
        );
    }

    private void validateSufficientBalance(Wallet wallet, long amount) {
        if (amount > wallet.getBalance())
            throw new PaymentException(WALLET_INSUFFICIENT_BALANCE);
    }

    private ResDeductDto applyDeductAndCreateTransaction(
            Wallet wallet, long deductAmount, TransactionType transactionType, UUID auctionId, UUID bidId
    ) {

        long beforeBalance = wallet.getBalance();

        // 1. 지갑에서 금액 차감
        Wallet updated = wallet.withBalance(beforeBalance - deductAmount);
        walletRepository.save(updated);

        // 2. 거래 내역 생성
        WalletTransaction walletTransaction = walletTransactionRepository.save(
                WalletTransaction.builder()
                        .walletId(updated.getId())
                        .transactionType(transactionType)
                        .amount(deductAmount)
                        .holdStatus(transactionType == HOLD ? HOLD_ACTIVE : null)
                        .auctionId(auctionId)
                        .bidId(bidId)
                        .build()
        );

        return ResDeductDto.from(updated, walletTransaction, beforeBalance);
    }

    private void releasePreviousHoldIfExists(ReqDeductDto request, long newBidAmount) {

        // 1. 현재 최고 입찰금 조회
        Optional<WalletTransaction> holdOpt = walletTransactionRepository
                .findByAuctionIdAndTransactionTypeAndHoldStatus(
                        request.getAuctionId(),
                        HOLD,
                        HOLD_ACTIVE
                );

        if (holdOpt.isEmpty()) return;

        WalletTransaction prevHold = holdOpt.get();

        // 2. 새 입찰금이 이전 입찰금보다 작으면 예외 발생
        if (newBidAmount <= prevHold.getAmount()) {
            throw new PaymentException(WALLET_TRANSACTION_HOLD_AMOUNT_NOT_HIGHER_THAN_PREVIOUS);
        }

        // 3. 이전 입찰자의 입찰금 반환
        Wallet prevHoldWallet = walletRepository.findById(prevHold.getWalletId())
                .orElseThrow(() -> new PaymentException(WALLET_NOT_FOUND));

        WalletTransaction released = prevHold.withHoldStatus(HOLD_RELEASED);
        walletTransactionRepository.save(released);

        Wallet releasedWallet = prevHoldWallet.withBalance(
                prevHoldWallet.getBalance() + prevHold.getAmount()
        );
        walletRepository.save(releasedWallet);
    }


    private boolean isNotAdmin(String role) {
        return !ADMIN.equals(role);
    }

    private boolean isNotOwner(Wallet wallet, String userId) {
        return !wallet.getUserId().equals(userId);
    }
}

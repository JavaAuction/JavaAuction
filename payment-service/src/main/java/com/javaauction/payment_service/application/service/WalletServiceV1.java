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

    @Transactional
    public ResCreateWalletDto createWallet(ReqCreateWalletDto request) {
        Wallet wallet = walletRepository.save(
                Wallet.builder()
                        .userId(request.getUserId())
                        .build()
        );

        return ResCreateWalletDto.from(wallet);
    }

    public ResGetWallet getWallet(UUID walletId) {
        return ResGetWallet.from(findWalletById(walletId));
    }

    @Transactional
    public ResChargeDto charge(UUID walletId, ReqChargeDto request) {

        Wallet wallet = findWalletById(walletId);

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
    public ResWithdrawDto withdraw(UUID walletId, ReqWithdrawDto request) {

        Wallet wallet = findWalletById(walletId);

        long beforeBalance = wallet.getBalance();
        long withdrawalAmount = request.getWithdrawAmount();

        if (withdrawalAmount > beforeBalance)
            throw new PaymentException(WALLET_INSUFFICIENT_BALANCE);

        Wallet withdrew = wallet.withBalance(beforeBalance - withdrawalAmount);
        walletRepository.save(withdrew);

        WalletTransaction walletTransaction = walletTransactionRepository.save(
                WalletTransaction.builder()
                        .walletId(withdrew.getId())
                        .transactionType(WITHDRAW)
                        .amount(withdrawalAmount)
                        .build()
        );

        return ResWithdrawDto.from(withdrew, walletTransaction, beforeBalance);
    }

    @Transactional
    public ResDeductDto deduct(ReqDeductDto request) {

        Wallet wallet = findWalletByUserId(request.getUserId());

        long beforeBalance = wallet.getBalance();
        long deductAmount = request.getDeductAmount();

        if (deductAmount > beforeBalance)
            throw new PaymentException(WALLET_INSUFFICIENT_BALANCE);

        TransactionType transactionType = request.getTransactionType();

        switch (transactionType) {
            case PAYMENT -> {}

            case HOLD -> {
                if (request.getBidId() == null)
                    throw new PaymentException(WALLET_MISSING_BID_ID);

                Optional<WalletTransaction> hold = walletTransactionRepository
                        .findByAuctionIdAndTransactionTypeAndHoldStatus(request.getAuctionId(), HOLD, HOLD_ACTIVE);

                if (hold.isPresent()) {
                    WalletTransaction prevHold = hold.get();

                    Wallet prevHoldWallet = walletRepository.findById(prevHold.getWalletId())
                            .orElseThrow(() -> new PaymentException(WALLET_NOT_FOUND));

                    WalletTransaction released = prevHold.withHoldStatus(HOLD_RELEASED);
                    walletTransactionRepository.save(released);

                    Wallet releasedWallet = prevHoldWallet.withBalance(prevHoldWallet.getBalance() + prevHold.getAmount());
                    walletRepository.save(releasedWallet);
                }
            }

            default -> throw new PaymentException(WALLET_INVALID_TRANSACTION_TYPE);
        }

        Wallet deduct = wallet.withBalance(beforeBalance - deductAmount);
        walletRepository.save(deduct);

        WalletTransaction walletTransaction = walletTransactionRepository.save(
                WalletTransaction.builder()
                        .walletId(deduct.getId())
                        .transactionType(transactionType)
                        .amount(deductAmount)
                        .holdStatus(transactionType == HOLD ? HOLD_ACTIVE : null)
                        .auctionId(request.getAuctionId())
                        .bidId(request.getBidId())
                        .build()
        );

        return ResDeductDto.from(deduct, walletTransaction, beforeBalance);
    }

    public Boolean validate(ReqValidateDto request) {

        Wallet wallet = findWalletByUserId(request.getUserId());

        return wallet.getBalance() >= request.getBidPrice();
    }

    private Wallet findWalletById(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new PaymentException(WALLET_NOT_FOUND));
    }

    private Wallet findWalletByUserId(String userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new PaymentException(WALLET_NOT_FOUND));
    }
}

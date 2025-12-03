package com.javaauction.payment_service.application.service;

import com.javaauction.payment_service.domain.enums.ExternalType;
import com.javaauction.payment_service.domain.enums.HoldStatus;
import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.domain.model.Wallet;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import com.javaauction.payment_service.domain.repository.WalletRepository;
import com.javaauction.payment_service.domain.repository.WalletTransactionRepository;
import com.javaauction.payment_service.presentation.advice.PaymentException;
import com.javaauction.payment_service.presentation.dto.request.*;
import com.javaauction.payment_service.presentation.dto.response.ResChargeDto;
import com.javaauction.payment_service.presentation.dto.response.ResCreateWalletDto;
import com.javaauction.payment_service.presentation.dto.response.ResDeductDto;
import com.javaauction.payment_service.presentation.dto.response.ResWithdrawDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.javaauction.payment_service.domain.enums.ExternalType.AUCTION;
import static com.javaauction.payment_service.domain.enums.ExternalType.BID;
import static com.javaauction.payment_service.domain.enums.HoldStatus.HOLD_ACTIVE;
import static com.javaauction.payment_service.domain.enums.TransactionType.CHARGE;
import static com.javaauction.payment_service.domain.enums.TransactionType.WITHDRAW;
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
        ExternalType externalType = null;
        HoldStatus holdStatus = null;

        switch (transactionType) {
            case PAYMENT -> externalType = AUCTION;
            case HOLD -> {
                externalType = BID;
                holdStatus = HOLD_ACTIVE;
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
                        .holdStatus(holdStatus)
                        .externalType(externalType)
                        .externalId(request.getExternalId())
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
        return walletRepository.findWalletByUserId(userId)
                .orElseThrow(() -> new PaymentException(WALLET_NOT_FOUND));
    }
}

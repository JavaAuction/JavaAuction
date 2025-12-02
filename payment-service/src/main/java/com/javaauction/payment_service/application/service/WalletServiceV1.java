package com.javaauction.payment_service.application.service;

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

import java.util.UUID;

import static com.javaauction.payment_service.domain.model.WalletTransaction.ExternalType.AUCTION;
import static com.javaauction.payment_service.domain.model.WalletTransaction.ExternalType.BID;
import static com.javaauction.payment_service.domain.model.WalletTransaction.HoldStatus.HOLD_ACTIVE;
import static com.javaauction.payment_service.domain.model.WalletTransaction.TransactionType.CHARGE;
import static com.javaauction.payment_service.domain.model.WalletTransaction.TransactionType.WITHDRAW;
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

        return ResCreateWalletDto.from(
                ResCreateWalletDto.WalletDto.from(wallet)
        );
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
                        .type(CHARGE)
                        .amount(chargeAmount)
                        .build()
        );

        ResChargeDto.WalletDto walletDto = ResChargeDto.WalletDto.from(charged, beforeBalance);
        ResChargeDto.WalletTransactionDto walletTransactionDto = ResChargeDto.WalletTransactionDto.from(walletTransaction);

        return ResChargeDto.from(walletDto, walletTransactionDto);
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
                        .type(WITHDRAW)
                        .amount(withdrawalAmount)
                        .build()
        );

        WalletDto walletDto = WalletDto.from(withdrew, beforeBalance);
        WalletTransactionDto walletTransactionDto = WalletTransactionDto.from(walletTransaction);

        return ResWithdrawDto.from(walletDto, walletTransactionDto);
    }

    @Transactional
    public ResDeductDto deduct(ReqDeductDto request) {

        Wallet wallet = findWalletByUserId(request.getUserId());

        long beforeBalance = wallet.getBalance();
        long deductAmount = request.getDeductAmount();

        if (deductAmount > beforeBalance)
            throw new PaymentException(WALLET_INSUFFICIENT_BALANCE);

        WalletTransaction.TransactionType transactionType = request.getTransactionType();
        WalletTransaction.ExternalType externalType = null;
        WalletTransaction.HoldStatus holdStatus = null;

        switch (transactionType) {
            case PAYMENT -> externalType = AUCTION;
            case HOLD -> {
                externalType = BID;
                holdStatus = HOLD_ACTIVE;
            }

            default -> throw new PaymentException(WALLET_INVALID_TRANSACTION_TYPE);
        }

        Wallet payment = wallet.withBalance(beforeBalance - deductAmount);
        walletRepository.save(payment);

        WalletTransaction walletTransaction = walletTransactionRepository.save(
                WalletTransaction.builder()
                        .walletId(payment.getId())
                        .type(transactionType)
                        .amount(deductAmount)
                        .holdStatus(holdStatus)
                        .externalType(externalType)
                        .externalId(request.getExternalId())
                        .build()
        );

        WalletDto walletDto = WalletDto.from(payment, beforeBalance);
        WalletTransactionDto walletTransactionDto = WalletTransactionDto.from(walletTransaction);

        return ResDeductDto.from(walletDto, walletTransactionDto);
    }

    public ResValidateDto validate(ReqValidateDto request) {

        Wallet wallet = findWalletByUserId(request.getUserId());

        long balance = wallet.getBalance();
        long bidPrice = request.getBidPrice();

        boolean isValid = balance >= bidPrice;

        return ResValidateDto.builder()
                .valid(isValid)
                .reasonCode(!isValid ? "INSUFFICIENT_BALANCE" : null)
                .reasonMessage(!isValid ? "잔액 부족" : null)
                .requiredAmount(!isValid ? bidPrice : null)
                .currentBalance(!isValid ? balance : null)
                .build();
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

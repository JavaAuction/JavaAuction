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

import static com.javaauction.payment_service.domain.model.WalletTransaction.HoldStatus.HOLD_ACTIVE;
import static com.javaauction.payment_service.domain.model.WalletTransaction.TransactionType.*;
import static com.javaauction.payment_service.presentation.advice.PaymentErrorCode.WALLET_INSUFFICIENT_BALANCE;
import static com.javaauction.payment_service.presentation.advice.PaymentErrorCode.WALLET_NOT_FOUND;

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
        long chargeAmount = request.getAmount();

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
        long withdrawalAmount = request.getAmount();

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
    public ResPaymentDto payment(ReqPaymentDto request) {

        Wallet wallet = findWalletByUserId(request.getUserId());

        long beforeBalance = wallet.getBalance();
        long paymentAmount = request.getAmount();

        if (paymentAmount > beforeBalance)
            throw new PaymentException(WALLET_INSUFFICIENT_BALANCE);

        Wallet payment = wallet.withBalance(beforeBalance - paymentAmount);
        walletRepository.save(payment);

        WalletTransaction walletTransaction = walletTransactionRepository.save(
                WalletTransaction.builder()
                        .walletId(payment.getId())
                        .type(PAYMENT)
                        .amount(paymentAmount)
                        .auctionId(request.getAuctionId())
                        .build()
        );

        WalletDto walletDto = WalletDto.from(payment, beforeBalance);
        WalletTransactionDto walletTransactionDto = WalletTransactionDto.from(walletTransaction);

        return ResPaymentDto.from(walletDto, walletTransactionDto);
    }

    @Transactional
    public ResHoldDto hold(ReqHoldDto request) {

        Wallet wallet = findWalletByUserId(request.getUserId());

        long beforeBalance = wallet.getBalance();
        long holdAmount = request.getAmount();

        if (holdAmount > beforeBalance)
            throw new PaymentException(WALLET_INSUFFICIENT_BALANCE);

        Wallet hold = wallet.withBalance(beforeBalance - holdAmount);
        walletRepository.save(hold);

        WalletTransaction walletTransaction = walletTransactionRepository.save(
                WalletTransaction.builder()
                        .walletId(hold.getId())
                        .type(HOLD)
                        .amount(holdAmount)
                        .holdStatus(HOLD_ACTIVE)
                        .bidId(request.getBidId())
                        .build()
        );

        WalletDto walletDto = WalletDto.from(hold, beforeBalance);
        WalletTransactionDto walletTransactionDto = WalletTransactionDto.from(walletTransaction);

        return ResHoldDto.from(walletDto, walletTransactionDto);
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

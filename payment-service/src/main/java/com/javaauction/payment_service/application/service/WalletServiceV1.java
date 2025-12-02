package com.javaauction.payment_service.application.service;

import com.javaauction.payment_service.domain.model.Wallet;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import com.javaauction.payment_service.domain.repository.WalletRepository;
import com.javaauction.payment_service.domain.repository.WalletTransactionRepository;
import com.javaauction.payment_service.presentation.advice.PaymentException;
import com.javaauction.payment_service.presentation.dto.request.ReqChargeDto;
import com.javaauction.payment_service.presentation.dto.request.ReqCreateWalletDto;
import com.javaauction.payment_service.presentation.dto.request.ReqWithdrawDto;
import com.javaauction.payment_service.presentation.dto.response.ResChargeDto;
import com.javaauction.payment_service.presentation.dto.response.ResCreateWalletDto;
import com.javaauction.payment_service.presentation.dto.response.ResWithdrawDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.javaauction.payment_service.domain.model.WalletTransaction.ExternalType.AUCTION;
import static com.javaauction.payment_service.domain.model.WalletTransaction.ExternalType.BID;
import static com.javaauction.payment_service.domain.model.WalletTransaction.HoldStatus.HOLD_ACTIVE;
import static com.javaauction.payment_service.domain.model.WalletTransaction.TransactionType.CHARGE;
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
    public ResWithdrawDto withdrawal(UUID walletId, ReqWithdrawDto request) {

        Wallet wallet = findWalletById(walletId);

        long beforeBalance = wallet.getBalance();
        long withdrawalAmount = request.getAmount();

        WalletTransaction.TransactionType transactionType = request.getTransactionType();
        WalletTransaction.HoldStatus holdStatus = null;

        if (withdrawalAmount > beforeBalance)
            throw new PaymentException(PAYMENT_INSUFFICIENT_BALANCE);

        switch (transactionType) {
            case WITHDRAW -> {}

            case PAYMENT -> {
                if (request.getExternalType() != AUCTION)
                    throw new PaymentException(PAYMENT_INVALID_EXTERNAL_TYPE);
                if (request.getExternalId() == null)
                    throw new PaymentException(PAYMENT_MISSING_EXTERNAL_ID);
            }

            case HOLD -> {
                if (request.getExternalType() != BID)
                    throw new PaymentException(PAYMENT_INVALID_EXTERNAL_TYPE);
                if (request.getExternalId() == null)
                    throw new PaymentException(PAYMENT_MISSING_EXTERNAL_ID);

                holdStatus = HOLD_ACTIVE;
            }

            default -> throw new PaymentException(PAYMENT_INVALID_TRANSACTION_TYPE);
        }

        Wallet withdrew = wallet.withBalance(beforeBalance - withdrawalAmount);
        walletRepository.save(withdrew);

        WalletTransaction walletTransaction = walletTransactionRepository.save(
                WalletTransaction.builder()
                        .walletId(withdrew.getId())
                        .type(transactionType)
                        .amount(withdrawalAmount)
                        .holdStatus(holdStatus)
                        .externalType(request.getExternalType())
                        .externalId(request.getExternalId())
                        .build()
        );

        ResWithdrawDto.WalletDto walletDto = ResWithdrawDto.WalletDto.from(withdrew, beforeBalance);
        ResWithdrawDto.WalletTransactionDto walletTransactionDto = ResWithdrawDto.WalletTransactionDto.from(walletTransaction);

        return ResWithdrawDto.from(walletDto, walletTransactionDto);
    }

    private Wallet findWalletById(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new PaymentException(PAYMENT_WALLET_NOT_FOUND));
    }
}

package com.javaauction.payment_service.application.service;

import com.javaauction.payment_service.domain.model.Wallet;
import com.javaauction.payment_service.domain.repository.WalletRepository;
import com.javaauction.payment_service.presentation.advice.PaymentException;
import com.javaauction.payment_service.presentation.dto.request.ReqChargeDto;
import com.javaauction.payment_service.presentation.dto.request.ReqCreateWalletDto;
import com.javaauction.payment_service.presentation.dto.response.ResChargeDto;
import com.javaauction.payment_service.presentation.dto.response.ResCreateWalletDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.javaauction.payment_service.presentation.advice.PaymentErrorCode.PAYMENT_WALLET_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletServiceV1 {

    private final WalletRepository walletRepository;

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

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new PaymentException(PAYMENT_WALLET_NOT_FOUND));

        long beforeBalance = wallet.getBalance();
        long chargeAmount = request.getAmount();

        Wallet charged = wallet.withBalance(beforeBalance + chargeAmount);
        walletRepository.save(charged);

        return ResChargeDto.from(charged, request.getAmount(), beforeBalance);
    }
}

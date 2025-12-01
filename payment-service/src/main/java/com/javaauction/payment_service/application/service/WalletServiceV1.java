package com.javaauction.payment_service.application.service;

import com.javaauction.payment_service.domain.model.Wallet;
import com.javaauction.payment_service.domain.repository.WalletRepository;
import com.javaauction.payment_service.presentation.dto.request.ReqCreateWalletDto;
import com.javaauction.payment_service.presentation.dto.response.ResCreateWalletDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}

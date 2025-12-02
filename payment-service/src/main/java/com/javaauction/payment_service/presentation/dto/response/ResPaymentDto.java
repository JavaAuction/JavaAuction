package com.javaauction.payment_service.presentation.dto.response;

import com.javaauction.payment_service.domain.model.Wallet;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ResPaymentDto {

    private WalletDto wallet;
    private WalletTransactionDto walletTransaction;

    public static ResPaymentDto from(WalletDto wallet, WalletTransactionDto walletTransaction) {
        return ResPaymentDto.builder()
                .wallet(wallet)
                .walletTransaction(walletTransaction)
                .build();
    }
}

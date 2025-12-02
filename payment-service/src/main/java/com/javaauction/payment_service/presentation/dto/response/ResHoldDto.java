package com.javaauction.payment_service.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResHoldDto {

    private WalletDto wallet;
    private WalletTransactionDto walletTransaction;

    public static ResHoldDto from(WalletDto wallet, WalletTransactionDto walletTransaction) {
        return ResHoldDto.builder()
                .wallet(wallet)
                .walletTransaction(walletTransaction)
                .build();
    }
}

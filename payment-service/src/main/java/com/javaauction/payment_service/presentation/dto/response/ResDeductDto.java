package com.javaauction.payment_service.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResDeductDto {

    private WalletDto wallet;
    private WalletTransactionDto walletTransaction;

    public static ResDeductDto from(WalletDto wallet, WalletTransactionDto walletTransaction) {
        return ResDeductDto.builder()
                .wallet(wallet)
                .walletTransaction(walletTransaction)
                .build();
    }
}

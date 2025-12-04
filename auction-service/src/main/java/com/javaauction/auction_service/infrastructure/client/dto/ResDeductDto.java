package com.javaauction.auction_service.infrastructure.client.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResDeductDto {

    private WalletDto wallet;
    private WalletTransactionDto walletTransaction;

    public static ResDeductDto from(
        WalletDto wallet, WalletTransactionDto walletTransaction) {
        return ResDeductDto.builder()
            .wallet(wallet)
            .walletTransaction(walletTransaction)
            .build();
    }

    @Getter
    @Builder
    public static class WalletDto {

        private UUID walletId;
        private Long beforeBalance;
        private Long afterBalance;

    }

    @Getter
    @Builder
    public static class WalletTransactionDto {

        private UUID walletTransactionId;
        private Long amount;
        private TransactionType transactionType;

    }

    public enum TransactionType {
        CHARGE, WITHDRAW, PAYMENT, HOLD
    }
}

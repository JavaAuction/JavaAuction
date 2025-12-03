package com.javaauction.auction_service.infrastructure.client.dto;

import com.javaauction.payment_service.domain.model.Wallet;
import com.javaauction.payment_service.domain.model.WalletTransaction;
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

        public static com.javaauction.payment_service.presentation.dto.response.WalletDto from(
            Wallet wallet, Long beforeBalance) {
            return com.javaauction.payment_service.presentation.dto.response.WalletDto.builder()
                .walletId(wallet.getId())
                .beforeBalance(beforeBalance)
                .afterBalance(wallet.getBalance())
                .build();
        }
    }

    @Getter
    @Builder
    public static class WalletTransactionDto {

        private UUID walletTransactionId;
        private Long amount;
        private WalletTransaction.TransactionType transactionType;

        public static com.javaauction.payment_service.presentation.dto.response.WalletTransactionDto from(
            WalletTransaction walletTransaction) {
            return com.javaauction.payment_service.presentation.dto.response.WalletTransactionDto.builder()
                .walletTransactionId(walletTransaction.getId())
                .transactionType(walletTransaction.getType())
                .amount(walletTransaction.getAmount())
                .build();
        }
    }
}

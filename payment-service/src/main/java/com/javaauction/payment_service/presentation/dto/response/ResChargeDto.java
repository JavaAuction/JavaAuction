package com.javaauction.payment_service.presentation.dto.response;

import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.domain.model.Wallet;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ResChargeDto {

    private UUID walletId;
    private UUID walletTransactionId;
    private Long chargeAmount;
    private Long beforeBalance;
    private Long afterBalance;
    private TransactionType transactionType;

    public static ResChargeDto from(Wallet wallet, WalletTransaction walletTransaction, Long beforeBalance) {
        return ResChargeDto.builder()
                .walletId(wallet.getId())
                .walletTransactionId(walletTransaction.getId())
                .chargeAmount(walletTransaction.getAmount())
                .beforeBalance(beforeBalance)
                .afterBalance(wallet.getBalance())
                .transactionType(walletTransaction.getTransactionType())
                .build();
    }

    @Getter
    @Builder
    public static class WalletDto {
        private UUID walletId;
        private Long beforeCharge;
        private Long afterCharge;

        public static WalletDto from(Wallet wallet, Long beforeCharge) {
            return WalletDto.builder()
                    .walletId(wallet.getId())
                    .beforeCharge(beforeCharge)
                    .afterCharge(wallet.getBalance())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class WalletTransactionDto {
        private UUID walletTransactionId;
        private Long chargeAmount;
        private TransactionType transactionType;

        public static WalletTransactionDto from(WalletTransaction walletTransaction) {
            return WalletTransactionDto.builder()
                    .walletTransactionId(walletTransaction.getId())
                    .transactionType(walletTransaction.getTransactionType())
                    .chargeAmount(walletTransaction.getAmount())
                    .build();
        }
    }
}

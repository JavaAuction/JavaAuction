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

    private WalletDto wallet;
    private WalletTransactionDto walletTransaction;

    public static ResChargeDto from(WalletDto walletDto, WalletTransactionDto walletTransactionDto) {
        return ResChargeDto.builder()
                .wallet(walletDto)
                .walletTransaction(walletTransactionDto)
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

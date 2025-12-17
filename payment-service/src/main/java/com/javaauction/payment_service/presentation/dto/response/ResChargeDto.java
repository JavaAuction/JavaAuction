package com.javaauction.payment_service.presentation.dto.response;

import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.domain.model.Wallet;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Schema(description = "현금 충전 요청 DTO")
@Getter
@Builder
public class ResChargeDto {

    @Schema(description = "지갑 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID walletId;

    @Schema(description = "거래내역 ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID walletTransactionId;

    @Schema(description = "충전 금액", example = "200000")
    private Long chargeAmount;

    @Schema(description = "충전 전 잔액", example = "300000")
    private Long beforeBalance;

    @Schema(description = "충전 후 잔액", example = "500000")
    private Long afterBalance;

    @Schema(description = "거래 유형", defaultValue = "CHARGE")
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

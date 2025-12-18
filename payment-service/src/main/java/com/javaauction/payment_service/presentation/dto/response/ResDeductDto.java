package com.javaauction.payment_service.presentation.dto.response;

import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.domain.model.Wallet;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Schema(description = "잔액 차감 요청 DTO")
@Getter
@Builder
public class ResDeductDto {

    @Schema(description = "지갑 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID walletId;

    @Schema(description = "", example = "fc45a010-c1f5-4e19-8eee-9fc58e23323d")
    private UUID walletTransactionId;

    @Schema(description = "차감 금액", example = "300000")
    private Long deductAmount;

    @Schema(description = "차감 전 잔액", example = "500000")
    private Long beforeBalance;

    @Schema(description = "차감 후 잔액", example = "200000")
    private Long afterBalance;

    @Schema(description = "거래 유형", example = "HOLD")
    private TransactionType transactionType;

    public static ResDeductDto from(Wallet wallet, WalletTransaction walletTransaction, Long beforeBalance) {
        return ResDeductDto.builder()
                .walletId(wallet.getId())
                .walletTransactionId(walletTransaction.getId())
                .deductAmount(walletTransaction.getAmount())
                .beforeBalance(beforeBalance)
                .afterBalance(wallet.getBalance())
                .transactionType(walletTransaction.getTransactionType())
                .build();
    }
}

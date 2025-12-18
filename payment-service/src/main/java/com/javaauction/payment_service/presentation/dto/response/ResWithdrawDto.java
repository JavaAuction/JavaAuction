package com.javaauction.payment_service.presentation.dto.response;

import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.domain.model.Wallet;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Schema(description = "현금 출금 응답 DTO")
@Getter
@Builder
public class ResWithdrawDto {

    @Schema(description = "지갑 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID walletId;

    @Schema(description = "거래내역 ID", example = "fc45a010-c1f5-4e19-8eee-9fc58e23323d")
    private UUID walletTransactionId;

    @Schema(description = "출금 금액", example = "300000")
    private Long withdrawAmount;

    @Schema(description = "출금 전 잔액", example = "500000")
    private Long beforeBalance;

    @Schema(description = "출금 후 잔액", example = "200000")
    private Long afterBalance;

    @Schema(description = "거래 유형", defaultValue = "WITHDRAWAL")
    private TransactionType transactionType;

    public static ResWithdrawDto from(Wallet wallet, WalletTransaction walletTransaction, Long beforeBalance) {
        return ResWithdrawDto.builder()
                .walletId(wallet.getId())
                .walletTransactionId(walletTransaction.getId())
                .withdrawAmount(walletTransaction.getAmount())
                .beforeBalance(beforeBalance)
                .afterBalance(wallet.getBalance())
                .transactionType(walletTransaction.getTransactionType())
                .build();
    }
}

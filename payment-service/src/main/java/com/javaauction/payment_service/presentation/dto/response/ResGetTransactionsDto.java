package com.javaauction.payment_service.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.javaauction.payment_service.domain.enums.HoldStatus;
import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Schema(description = "거래내역 조회 응답 DTO")
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResGetTransactionsDto {

    @Schema(description = "거래내역 ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID transactionId;

    @Schema(description = "거래 금액", example = "500000")
    private Long amount;

    @Schema(description = "거래 유형", example = "HOLD")
    private TransactionType transactionType;

    @Schema(description = "예치금 홀딩 상태", example = "HOLD_ACTIVE")
    private HoldStatus holdStatus;

    public static ResGetTransactionsDto from(WalletTransaction walletTransaction) {
        return ResGetTransactionsDto.builder()
                .transactionId(walletTransaction.getId())
                .amount(walletTransaction.getAmount())
                .transactionType(walletTransaction.getTransactionType())
                .holdStatus(walletTransaction.getHoldStatus())
                .build();
    }
}

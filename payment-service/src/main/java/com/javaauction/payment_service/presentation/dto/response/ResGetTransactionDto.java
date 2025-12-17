package com.javaauction.payment_service.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.javaauction.payment_service.domain.enums.HoldStatus;
import com.javaauction.payment_service.domain.enums.TransactionType;
import com.javaauction.payment_service.domain.model.WalletTransaction;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Schema(description = "특정 거래내역 조회 응답 DTO")
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResGetTransactionDto {

    @Schema(description = "거래내역 ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID walletTransactionId;

    @Schema(description = "거래 금액", example = "500000")
    private Long amount;

    @Schema(description = "거래 유형", example = "PAYMENT")
    private TransactionType transactionType;

    @Schema(description = "입찰금 홀딩 상태", example = "HOLD_RELEASE")
    private HoldStatus holdStatus;

    @Schema(description = "경매 ID", example = "61085675-54d7-48fb-b831-b43bb0899687")
    private UUID auctionId;

    @Schema(description = "입찰 ID", example = "316a9689-9b88-4b21-a8d3-345afdcb135f")
    private UUID bidId;

    public static ResGetTransactionDto fromDomain(WalletTransaction walletTransaction) {
        return ResGetTransactionDto.builder()
                .walletTransactionId(walletTransaction.getId())
                .amount(walletTransaction.getAmount())
                .transactionType(walletTransaction.getTransactionType())
                .holdStatus(walletTransaction.getHoldStatus())
                .auctionId(walletTransaction.getAuctionId())
                .bidId(walletTransaction.getBidId())
                .build();
    }
}

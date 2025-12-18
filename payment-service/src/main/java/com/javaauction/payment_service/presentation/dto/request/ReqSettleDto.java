package com.javaauction.payment_service.presentation.dto.request;

import com.javaauction.payment_service.domain.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Schema(description = "경매 정산 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqSettleDto {

    @Schema(description = "거래 유형", example = "HOLD")
    @NotNull
    private TransactionType transactionType;

    @Schema(description = "구매자 ID", example = "isaac6")
    @NotNull
    private String buyerId;

    @Schema(description = "판매자 ID", example = "noah11")
    @NotNull
    private String sellerId;

    @Schema(description = "경매 ID", example = "bce3b58e-37bb-4fe4-b94f-ee523977acdc")
    @NotNull
    private UUID auctionId;

    @Schema(description = "입찰 ID", example = "3307a2e9-9c9d-49cd-87bc-5b6045f60e89")
    @NotNull
    private Long amount;
}

package com.javaauction.payment_service.presentation.dto.request;

import com.javaauction.payment_service.domain.enums.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Schema(description = "잔액 차감 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqDeductDto {

    @Schema(description = "사용자 ID", example = "isaac6")
    @NotNull
    private String userId;

    @Schema(description = "거래 유형", example = "HOLD")
    @NotNull
    private TransactionType transactionType;

    @Schema(description = "차감 금액", example = "360000")
    @NotNull
    @Min(1)
    private Long deductAmount;

    @Schema(description = "옥션 ID", example = "b8aebb10-15f5-454d-8c1c-75e3c01e0d13")
    @NotNull
    private UUID auctionId;

    @Schema(description = "입찰 ID", example = "3307a2e9-9c9d-49cd-87bc-5b6045f60e89")
    private UUID bidId;
}

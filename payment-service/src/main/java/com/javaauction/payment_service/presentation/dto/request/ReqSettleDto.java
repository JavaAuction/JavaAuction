package com.javaauction.payment_service.presentation.dto.request;

import com.javaauction.payment_service.domain.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqSettleDto {

    @NotNull
    private TransactionType transactionType;

    @NotNull
    private String buyerId;

    @NotNull
    private String sellerId;

    @NotNull
    private UUID auctionId;

    @NotNull
    private Long amount;
}

package com.javaauction.auction_service.infrastructure.client.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ReqSettleDto(

    @NotNull TransactionType transactionType,

    @NotNull String buyerId,

    @NotNull String sellerId,

    @NotNull UUID auctionId,

    @NotNull Long amount
) {

    public enum TransactionType {
        CHARGE, WITHDRAWAL, PAYMENT, HOLD, SELLER_PROCEED
    }
}

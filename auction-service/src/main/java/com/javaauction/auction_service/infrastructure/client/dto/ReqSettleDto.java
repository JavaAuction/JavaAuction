package com.javaauction.auction_service.infrastructure.client.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqSettleDto {

    private TransactionType transactionType;
    private String buyerId;
    private String sellerId;
    private UUID auctionId;
    private Long amount;
}


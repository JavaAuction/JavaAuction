package com.javaauction.user.infrastructure.external.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class InternalBidDto {

    private UUID bidId;
    private UUID auctionId;
    private UUID productId;
    private Long bidPrice;
    private String status;
    private Instant createdAt;
}

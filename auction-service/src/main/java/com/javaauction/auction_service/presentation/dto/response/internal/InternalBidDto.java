package com.javaauction.auction_service.presentation.dto.response.internal;

import com.javaauction.auction_service.domain.entity.enums.BidStatus;
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
    private BidStatus status;
    private Instant createdAt;
}

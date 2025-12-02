package com.javaauction.auction_service.presentation.dto.response;

import com.javaauction.auction_service.domain.entity.enums.BidStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ResBuyNowDto {
    private UUID auctionId;
    private UUID productId;
    private String buyerId;
    private Long finalPrice;
    private Instant purchasedAt;
}


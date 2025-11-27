package com.javaauction.auction_service.presentation.dto;

import com.javaauction.auction_service.domain.entity.enums.AuctionStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record ResGetAuctionDto(
    UUID auctionId,
    UUID productId,
    Long startPrice,
    Long currentPrice,
    UUID successfulBidder,
    Long unit,
    Boolean buyNowEnable,
    Long buyNowPrice,
    LocalDateTime endAt,
    AuctionStatus status
) {

}

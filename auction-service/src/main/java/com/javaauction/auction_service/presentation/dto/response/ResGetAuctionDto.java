package com.javaauction.auction_service.presentation.dto.response;

import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.entity.enums.AuctionStatus;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ResGetAuctionDto(
    UUID auctionId,
    UUID productId,
    Long startPrice,
    Long currentPrice,
    String successfulBidder,
    Long unit,
    Boolean buyNowEnable,
    Long buyNowPrice,
    LocalDateTime endAt,
    AuctionStatus status,
    Instant createdAt,
    String createdBy
) {

    public static ResGetAuctionDto from(Auction auction) {
        return ResGetAuctionDto.builder()
            .auctionId(auction.getAuctionId())
            .productId(auction.getProductId())
            .startPrice(auction.getStartPrice())
            .currentPrice(auction.getCurrentPrice())
            .successfulBidder(auction.getSuccessfulBidder())
            .unit(auction.getUnit())
            .buyNowEnable(auction.getBuyNowEnable())
            .buyNowPrice(auction.getBuyNowPrice())
            .endAt(auction.getEndedAt())
            .status(auction.getStatus())
            .createdAt(auction.getCreatedAt())
            .createdBy(auction.getCreatedBy())
            .build();
    }

}

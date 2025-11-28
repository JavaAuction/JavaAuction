package com.javaauction.auction_service.presentation.dto.response;

import com.javaauction.auction_service.domain.entity.Auction;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ResCreatedAuctionDto(
    UUID id
) {

    public static ResCreatedAuctionDto from(Auction auction) {
        return ResCreatedAuctionDto.builder().id(auction.getAuctionId()).build();
    }
}

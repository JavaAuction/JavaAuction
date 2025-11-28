package com.javaauction.auction_service.presentation.dto.response;

import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.entity.enums.AuctionStatus;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record ResGetAuctionsDto(
    Page<AuctionDTO> auctions
) {

    public static ResGetAuctionsDto from(
        Page<Auction> auctions
    ) {

        return ResGetAuctionsDto.builder()
            .auctions(auctions.map(AuctionDTO::from))
            .build();
    }

    @Builder
    public record AuctionDTO(
        UUID auctionId,
        UUID productId,
        String productName,
        Long currentPrice,
        boolean buyNowEnable,
        LocalDateTime endAt,
        AuctionStatus status,
        Instant createdAt
    ) {

        public static AuctionDTO from(Auction auction) {
            return AuctionDTO.builder()
                .auctionId(auction.getAuctionId())
                .productId(auction.getProductId())
                .productName(auction.getProductName())
                .currentPrice(auction.getCurrentPrice())
                .buyNowEnable(auction.getBuyNowEnable())
                .endAt(auction.getEndedAt())
                .status(auction.getStatus())
                .createdAt(auction.getCreatedAt())
                .build();
        }

    }
}

package com.javaauction.auction_service.presentation.controller.dto;

import com.javaauction.auction_service.domain.model.enums.AuctionStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;

public record ResGetAuctionsDto(
    Page<AuctionDTO> auctionList
) {

    public record AuctionDTO(
        UUID auctionId,
        UUID productId,
        Long currentPrice,
        LocalDateTime endAt,
        AuctionStatus status
    ) {

    }
}

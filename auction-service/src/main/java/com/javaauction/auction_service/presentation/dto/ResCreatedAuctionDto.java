package com.javaauction.auction_service.presentation.dto;

import java.util.UUID;
import lombok.Builder;

@Builder
public record ResCreatedAuctionDto(
    UUID id
) {

}

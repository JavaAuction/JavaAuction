package com.javaauction.auction_service.domain.event;

import lombok.Builder;

@Builder
public record AuctionValidationResponseEvent(
    String correlationId,
    boolean isValid,
    String sellerId,
    String buyerId
) {

}

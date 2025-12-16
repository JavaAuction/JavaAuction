package com.javaauction.auction_service.domain.event;

import java.util.UUID;
import lombok.Builder;

@Builder
public record AuctionValidationRequestEvent(
    UUID auctionId,
    String correlationId,
    String userId
) {

}

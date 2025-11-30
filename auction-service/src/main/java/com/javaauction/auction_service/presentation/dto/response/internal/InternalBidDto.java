package com.javaauction.auction_service.presentation.dto.response.internal;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class InternalBidDto {

    private UUID bidId;
    private UUID auctionId;
    private UUID productId;
    private Long bidPrice;
    private LocalDateTime createdAt;
}

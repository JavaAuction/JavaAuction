package com.javaauction.auction_service.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResGetBidsDto {

    private UUID auctionId;
    private UUID productId;
    private List<BidDto> bids;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class BidDto {
        private UUID bidId;
        private Long bidPrice;
        private LocalDateTime createdAt;
    }
}

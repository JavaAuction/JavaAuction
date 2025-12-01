package com.javaauction.auction_service.presentation.dto.response;

import com.javaauction.auction_service.domain.entity.enums.BidStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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
        private Instant createdAt;
        private BidStatus status;
    }
}

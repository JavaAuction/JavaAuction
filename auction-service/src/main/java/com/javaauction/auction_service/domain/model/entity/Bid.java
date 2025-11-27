package com.javaauction.auction_service.domain.model.entity;

import com.javaauction.auction_service.domain.model.enums.BidStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Bid {

    private UUID bidId;
    private UUID auctionId;
    private Long userId;
    private Long bidPrice;
    private BidStatus status;
    private LocalDateTime createdAt;

    protected Bid() {}

    private Bid(UUID auctionId, Long userId, Long bidPrice) {
        this.bidId = UUID.randomUUID();
        this.auctionId = auctionId;
        this.userId = userId;
        this.bidPrice = bidPrice;
        this.status = BidStatus.HELD;
        this.createdAt = LocalDateTime.now();
    }

    public static Bid create(UUID auctionId, Long userId, Long bidPrice) {
        return new Bid(auctionId, userId, bidPrice);
    }
}


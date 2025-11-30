package com.javaauction.auction_service.domain.event;

import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.entity.Bid;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class BidResult {

    private final Auction auction;
    private final String oldUserId;
    private final Long oldPrice;
    private final Bid newBid;

    public UUID getAuctionId() {
        return auction.getAuctionId();
    }
}

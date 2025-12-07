package com.javaauction.auction_service.domain.event;

import com.javaauction.auction_service.domain.entity.Auction;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class BuyNowEvent {
    private final Auction auction;
    private final String buyerId;
    private final long price;
    private final UUID tempBidId; // HOLD 용 임시 bidId
}


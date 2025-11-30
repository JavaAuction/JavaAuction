package com.javaauction.auction_service.domain.event;

import lombok.Getter;

@Getter
public class OldBidReleaseEvent {
    private final BidResult bidResult;

    public OldBidReleaseEvent(BidResult bidResult) {
        this.bidResult = bidResult;
    }
}

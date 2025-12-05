package com.javaauction.auction_service.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BidAlertEvent {
    private final BidResult result;
}



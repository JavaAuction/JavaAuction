package com.javaauction.auction_service.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BidGetRequestEvent {
    private String userId;
    private String correlationId;
}

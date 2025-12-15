package com.javaauction.auction_service.domain.event;

import com.javaauction.auction_service.presentation.dto.response.internal.ResInternalBidsDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BidGetResponseEvent {

    private String correlationId;
    private ResInternalBidsDto bids;
}


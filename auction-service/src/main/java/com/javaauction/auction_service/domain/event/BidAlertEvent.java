package com.javaauction.auction_service.domain.event;

import com.javaauction.auction_service.infrastructure.client.dto.AlertType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class BidAlertEvent {

    private final UUID auctionId;
    private final String auctionOwnerId; // 경매 등록자
    private final String productName;
    private final Long bidPrice;
    private final AlertType alertType;
}



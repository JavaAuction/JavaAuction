package com.javaauction.auction_service.domain.event;

import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
public class WalletDeductFailedEvent {

    private String userId;
    private UUID auctionId;
    private UUID bidId;
    private Long requestedAmount;

    private String errorCode;
    private String errorMessage;
}


package com.javaauction.auction_service.domain.event;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class WalletSettleFailedEvent {

    private UUID auctionId;
    private String buyerId;
    private String sellerId;
    private Long requestedAmount;

    private String errorCode;
    private String errorMessage;
}

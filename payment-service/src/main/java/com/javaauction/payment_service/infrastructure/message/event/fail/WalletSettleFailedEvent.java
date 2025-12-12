package com.javaauction.payment_service.infrastructure.message.event.fail;

import lombok.*;

import java.util.UUID;

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

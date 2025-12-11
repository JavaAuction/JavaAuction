package com.javaauction.payment_service.infrastructure.message.event.fail;

import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class WalletDeductFailedEvent {

    private String userId;
    private UUID auctionId;
    private UUID bidId;
    private Long requestedAmount;

    private String errorCode;
    private String errorMessage;
}

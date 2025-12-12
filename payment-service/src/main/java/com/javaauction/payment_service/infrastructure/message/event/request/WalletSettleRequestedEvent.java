package com.javaauction.payment_service.infrastructure.message.event.request;

import com.javaauction.payment_service.domain.enums.TransactionType;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class WalletSettleRequestedEvent {

    private UUID auctionId;
    private String buyerId;
    private String sellerId;
    private TransactionType transactionType;
    private Long amount;
}

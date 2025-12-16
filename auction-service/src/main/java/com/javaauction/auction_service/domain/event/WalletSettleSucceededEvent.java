package com.javaauction.auction_service.domain.event;

import com.javaauction.auction_service.infrastructure.client.dto.TransactionType;
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
public class WalletSettleSucceededEvent {

    private UUID auctionId;
    private String buyerId;
    private String sellerId;

    private Long amount;
    private TransactionType transactionType;
}

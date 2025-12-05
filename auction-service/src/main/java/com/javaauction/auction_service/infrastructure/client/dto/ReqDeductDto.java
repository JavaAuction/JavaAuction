package com.javaauction.auction_service.infrastructure.client.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
public class ReqDeductDto {
    private String userId;
    private DeductType transactionType;
    private Long deductAmount;
    private UUID auctionId;
    private UUID bidId;
}
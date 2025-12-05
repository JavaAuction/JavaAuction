package com.javaauction.auction_service.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqAlertDto {
    private UUID auctionId;
    private String userId;
    private AlertType alertType;
    private String content;
}

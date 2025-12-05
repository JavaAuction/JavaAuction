package com.javaauction.auction_service.infrastructure.client.dto;


import java.util.UUID;
import lombok.Builder;

@Builder
public record ReqPostInternalAlertsDtoV1(

    UUID auctionId,
    String userId,
    AlertType alertType,
    String content
) {

}

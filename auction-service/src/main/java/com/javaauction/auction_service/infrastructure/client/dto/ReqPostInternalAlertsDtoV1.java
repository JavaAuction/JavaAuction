package com.javaauction.auction_service.infrastructure.client.dto;

public record ReqPostInternalAlertsDtoV1(

    UUID auctionId,
    String userId,
    AlertType alertType,
    String content
) {

}

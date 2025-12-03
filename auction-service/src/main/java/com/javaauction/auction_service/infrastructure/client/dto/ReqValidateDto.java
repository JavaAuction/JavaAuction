package com.javaauction.auction_service.infrastructure.client.dto;

public record ReqValidateDto(

    String userId,
    Long bidPrice
) {

}


package com.javaauction.auction_service.presentation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReqUpdateAuctionDto(
    UUID productId,
    Long startPrice,
    Long unit,
    Boolean buyNowEnable,
    Long buyNowPrice,
    LocalDateTime endAt
) {

}

package com.javaauction.auction_service.presentation.dto.request;

import java.time.LocalDateTime;

public record ReqUpdateAuctionDto(
    String successfulBidder,
    Long startPrice,
    Long unit,
    Boolean buyNowEnable,
    Long buyNowPrice,
    LocalDateTime endedAt
) {

}

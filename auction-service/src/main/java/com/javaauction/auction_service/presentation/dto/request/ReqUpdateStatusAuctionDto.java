package com.javaauction.auction_service.presentation.dto.request;

import com.javaauction.auction_service.domain.entity.enums.AuctionStatus;

public record ReqUpdateStatusAuctionDto(
    AuctionStatus status
) {

}

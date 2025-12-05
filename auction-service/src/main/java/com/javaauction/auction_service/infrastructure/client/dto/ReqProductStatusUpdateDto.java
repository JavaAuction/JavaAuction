package com.javaauction.auction_service.infrastructure.client.dto;

import lombok.Builder;

@Builder
public record ReqProductStatusUpdateDto(
    ProductStatus productStatus,
    Long finalPrice
) {

    public enum ProductStatus {
        AUCTION_WAITING, // 경매 대기
        AUCTION_RUNNING, // 경매 중
        SOLD // 판매 완료
    }

}

package com.javaauction.chatservice.presentation.dto.response;

import java.time.Instant;
import java.util.UUID;

public record RepGetProductsDtoV1 (
        UUID productId,
        String userId,
        String name,
        String description,
        String imageUrl,
        Long finalPrice,
        ProductStatus productStatus,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {

    public enum ProductStatus {
        AUCTION_WAITING, // 경매 대기
        AUCTION_RUNNING, // 경매 중
        SOLD // 판매 완료
    }
}

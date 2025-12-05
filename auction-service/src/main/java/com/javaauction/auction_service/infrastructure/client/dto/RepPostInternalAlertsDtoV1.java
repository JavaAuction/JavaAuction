package com.javaauction.auction_service.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record RepPostInternalAlertsDtoV1(
    UUID alertId,
    String userId,
    UUID auctionId,
    AlertType alertType,
    String content,
    Boolean isRead,
    @JsonFormat(shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd HH:mm:ss",
        timezone = "Asia/Seoul")
    Instant createdAt
) {

}

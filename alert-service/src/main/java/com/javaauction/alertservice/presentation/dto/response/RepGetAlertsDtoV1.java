package com.javaauction.alertservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.javaauction.alertservice.domain.enums.AlertType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class RepGetAlertsDtoV1 {
    private UUID alertId;
    private UUID auctionId;
    private AlertType alertType;
    private String content;
    private Boolean isRead;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "Asia/Seoul")
    private Instant createdAt;

    @QueryProjection
    public RepGetAlertsDtoV1(UUID alertId, UUID auctionId, AlertType alertType, String content, Boolean isRead, Instant createdAt) {
        this.alertId = alertId;
        this.auctionId = auctionId;
        this.alertType = alertType;
        this.content = (content != null) ? content : "";
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

}

package com.javaauction.alertservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.javaauction.alertservice.domain.entity.Alert;
import com.javaauction.alertservice.domain.enums.AlertType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepPostInternalAlertsDtoV1 {

    private UUID alertId;
    private String userId;
    private UUID auctionId;
    private AlertType alertType;
    private String content;
    private Boolean isRead;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "Asia/Seoul")
    private Instant createdAt;

    public static RepPostInternalAlertsDtoV1 of(Alert alert) {
        return RepPostInternalAlertsDtoV1.builder()
                .alertId(alert.getAlertId())
                .userId(alert.getUserId())
                .auctionId(alert.getAuctionId())
                .alertType(alert.getAlertType())
                .content(alert.getContent())
                .isRead(alert.getIsRead())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}

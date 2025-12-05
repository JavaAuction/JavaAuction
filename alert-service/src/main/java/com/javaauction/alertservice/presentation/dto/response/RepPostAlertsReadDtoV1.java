package com.javaauction.alertservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.javaauction.alertservice.domain.entity.Alert;
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
public class RepPostAlertsReadDtoV1 {

    private UUID alertId;
    private Boolean isRead;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss",
            timezone = "Asia/Seoul")
    private Instant updatedAt;

    public static RepPostAlertsReadDtoV1 of(Alert alert) {
        return RepPostAlertsReadDtoV1.builder()
                .alertId(alert.getAlertId())
                .isRead(alert.getIsRead())
                .updatedAt(alert.getUpdatedAt())
                .build();
    }
}
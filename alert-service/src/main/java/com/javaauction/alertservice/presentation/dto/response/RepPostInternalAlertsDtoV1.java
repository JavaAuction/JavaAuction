package com.javaauction.alertservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.javaauction.alertservice.domain.enums.AlertType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepPostInternalAlertsDtoV1 {
    private UUID alertId;
    private String userId;
    private UUID productId;
    private AlertType alertType;
    private String content;
    private boolean isRead;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}

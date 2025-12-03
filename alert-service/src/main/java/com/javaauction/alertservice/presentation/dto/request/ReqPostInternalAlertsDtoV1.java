package com.javaauction.alertservice.presentation.dto.request;

import com.javaauction.alertservice.domain.enums.AlertType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqPostInternalAlertsDtoV1 {
    private UUID auctionId;
    private String userId;
    private AlertType alertType;
    private String content;
}

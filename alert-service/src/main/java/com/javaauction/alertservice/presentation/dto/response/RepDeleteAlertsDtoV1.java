package com.javaauction.alertservice.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepDeleteAlertsDtoV1 {

    private List<UUID> alertIds;
    private String message;

    public static RepDeleteAlertsDtoV1 of(List<UUID> alertIds) {
        String message = alertIds.isEmpty()
                ? "삭제할 알림을 찾을 수 없습니다."
                : alertIds.size() + "건의 알림이 삭제되었습니다.";

        return RepDeleteAlertsDtoV1.builder()
                .alertIds(alertIds)
                .message(message)
                .build();
    }
}
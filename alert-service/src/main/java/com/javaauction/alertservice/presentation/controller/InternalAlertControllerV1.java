package com.javaauction.alertservice.presentation.controller;

import com.javaauction.alertservice.application.service.AlertServiceV1;
import com.javaauction.alertservice.presentation.advice.AlertSuccessCode;
import com.javaauction.alertservice.presentation.dto.request.ReqPostInternalAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepPostInternalAlertsDtoV1;
import com.javaauction.global.presentation.response.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "내부 알림 컨트롤러",
        description = "다른 서비스에서 이용하는 알림 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/alerts")
public class InternalAlertControllerV1 {
    private final AlertServiceV1 alertService;

    @Operation(
            summary = "알림 생성 (내부용)",
            description = "다른 서비스에서 호출하여 사용자 알림을 생성하는 내부 전용 API입니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<RepPostInternalAlertsDtoV1>> createAlert(
            @RequestBody ReqPostInternalAlertsDtoV1 reqDto
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        AlertSuccessCode.ALERT_CREATE_SUCCESS,
                        alertService.postInternalAlerts(reqDto)
                )
        );
    }
}

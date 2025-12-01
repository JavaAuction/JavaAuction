package com.javaauction.alertservice.presentation.controller;

import com.javaauction.alertservice.application.service.AlertServiceV1;
import com.javaauction.alertservice.presentation.advice.AlertSuccessCode;
import com.javaauction.alertservice.presentation.dto.request.ReqPostInternalAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepPostInternalAlertsDtoV1;
import com.javaauction.global.presentation.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/alerts")
public class InternalAlertControllerV1 {
    private final AlertServiceV1 alertService;

    // 알림 생성
    @PostMapping
    public ResponseEntity<ApiResponse<RepPostInternalAlertsDtoV1>> createAlert(@RequestBody ReqPostInternalAlertsDtoV1 request) {
        RepPostInternalAlertsDtoV1 response = alertService.postInternalAlertsDtoV1(request);
        return ResponseEntity.ok(ApiResponse.success(AlertSuccessCode.ALERT_CREATE_SUCCESS, response));
    }
}

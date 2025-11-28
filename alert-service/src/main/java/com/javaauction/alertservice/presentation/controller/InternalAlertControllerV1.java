package com.javaauction.alertservice.presentation.controller;

import com.javaauction.alertservice.presentation.dto.request.ReqPostInternalAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepPostInternalAlertsDtoV1;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/alerts")
public class InternalAlertControllerV1 {
    // 알림 생성
    @PostMapping
    public ResponseEntity<RepPostInternalAlertsDtoV1> createAlert(@RequestBody ReqPostInternalAlertsDtoV1 request) {
        RepPostInternalAlertsDtoV1 createdAlert = new RepPostInternalAlertsDtoV1(
                UUID.randomUUID(),
                request.getUserId(),
                request.getProductId(),
                request.getAlertType(),
                "알림이 등록되었습니다.",
                false,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(createdAlert, HttpStatus.OK);
    }
}

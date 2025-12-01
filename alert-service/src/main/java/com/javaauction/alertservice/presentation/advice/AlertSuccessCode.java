package com.javaauction.alertservice.presentation.advice;

import com.javaauction.global.infrastructure.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum AlertSuccessCode implements ResponseCode {
    ALERT_FIND_SUCCESS(HttpStatus.OK, "ALERT200", "알림 조회 성공"),
    ALERT_CREATE_SUCCESS(HttpStatus.CREATED, "ALERT201", "알림 생성 성공");

    private final HttpStatus status;
    private final String code;
    private final String message;
;
}

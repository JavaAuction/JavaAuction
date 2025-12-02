package com.javaauction.alertservice.presentation.advice;

import com.javaauction.global.infrastructure.code.ResponseCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AlertErrorCode implements ResponseCode {
    ALERT_NOT_FOUND("ALERT000", "해당 알림을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ALERT_PRODUCT_NOT_FOUND("ALERT001", "해당 상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ALERT_UNAUTH("ALERT002", "일반 회원은 자신의 알림만 접근 및 삭제 가능합니다.", HttpStatus.UNAUTHORIZED);

    private final String code;
    private final String message;
    private final HttpStatus status;

    AlertErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

}

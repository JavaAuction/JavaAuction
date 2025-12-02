package com.javaauction.payment_service.presentation.advice;

import com.javaauction.global.infrastructure.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ResponseCode {

    WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT001", "일치하는 지갑을 찾을 수 없습니다."),
    WALLET_INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "PAYMENT002", "잔액이 부족합니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}

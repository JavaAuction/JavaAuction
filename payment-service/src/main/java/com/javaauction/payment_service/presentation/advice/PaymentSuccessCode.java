package com.javaauction.payment_service.presentation.advice;

import com.javaauction.global.infrastructure.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentSuccessCode implements ResponseCode {

    PAYMENT_WALLET_CREATE_SUCCESS(HttpStatus.CREATED, "PAYMENT200", "지갑 생성 성공"),
    PAYMENT_CHARGE_SUCCESS(HttpStatus.OK, "PAYMENT201", "잔액 충전 성공"),
    PAYMENT_WITHDRAW_SUCCESS(HttpStatus.OK, "PAYMENT202", "잔액 출금 성공"),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
    }

package com.javaauction.payment_service.presentation.advice;

import com.javaauction.global.infrastructure.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ResponseCode {

    PAYMENT_WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT001", "일치하는 지갑을 찾을 수 없습니다."),
    PAYMENT_INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "PAYMENT002", "잔액이 부족합니다."),
    PAYMENT_INVALID_TRANSACTION_TYPE(HttpStatus.BAD_REQUEST, "PAYMENT003", "출금 거래 유형이 유효하지 않습니다."),
    PAYMENT_INVALID_EXTERNAL_TYPE(HttpStatus.BAD_REQUEST, "PAYMENT004", "즉시 결제 요청의 외부 서비스 유형이 유효하지 않습니다."),
    PAYMENT_MISSING_EXTERNAL_ID(HttpStatus.BAD_REQUEST, "PAYMENT005", "즉시 결제 요청 시 경매 ID는 반드시 포함되어야 합니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}

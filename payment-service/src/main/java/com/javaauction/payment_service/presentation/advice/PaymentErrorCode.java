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
    WALLET_INVALID_TRANSACTION_TYPE(HttpStatus.BAD_REQUEST, "PAYMENT003", "거래 유형이 유효하지 않습니다."),
    WALLET_TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT004", "일치하는 거래 내역을 찾을 수 없습니다."),
    WALLET_TRANSACTION_INVALID_RELATION(HttpStatus.BAD_REQUEST, "PAYMENT005", "요청 지갑에서 일치하는 거래 내역을 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}

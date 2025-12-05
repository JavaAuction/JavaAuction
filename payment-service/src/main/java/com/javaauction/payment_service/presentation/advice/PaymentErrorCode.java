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
    WALLET_MISSING_BID_ID(HttpStatus.BAD_REQUEST, "PAYMENT004", "입찰 식별자가 누락되었습니다."),
    WALLET_TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT005", "일치하는 거래 내역을 찾을 수 없습니다."),
    WALLET_TRANSACTION_INVALID_RELATION(HttpStatus.BAD_REQUEST, "PAYMENT006", "요청 지갑에서 일치하는 거래 내역을 찾을 수 없습니다."),
    WALLET_TRANSACTION_HOLD_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT007", "예치금 정보가 존재하지 않습니다."),
    WALLET_TRANSACTION_HOLD_AMOUNT_NOT_HIGHER_THAN_PREVIOUS(HttpStatus.BAD_REQUEST, "PAYMENT008", "상회 입찰 금액이 기존 입찰금보다 높아야 합니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}

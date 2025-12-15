package com.javaauction.payment_service.presentation.advice;

import com.javaauction.global.infrastructure.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ResponseCode {

    WALLET_ACCESS_DENIED(HttpStatus.FORBIDDEN, "PAYMENT-000", "접근 권한이 없습니다."),
    WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT-001", "일치하는 지갑을 찾을 수 없습니다."),
    WALLET_INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "PAYMENT-002", "잔액이 부족합니다."),
    WALLET_INVALID_TRANSACTION_TYPE(HttpStatus.BAD_REQUEST, "PAYMENT-003", "거래 유형이 유효하지 않습니다."),
    WALLET_MISSING_BID_ID(HttpStatus.BAD_REQUEST, "PAYMENT-004", "입찰 식별자가 누락되었습니다."),
    WALLET_TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT-005", "일치하는 거래 내역을 찾을 수 없습니다."),
    WALLET_TRANSACTION_INVALID_RELATION(HttpStatus.BAD_REQUEST, "PAYMENT-006", "요청 지갑에서 일치하는 거래 내역을 찾을 수 없습니다."),
    WALLET_TRANSACTION_HOLD_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT-007", "예치금 정보가 존재하지 않습니다."),
    WALLET_TRANSACTION_HOLD_AMOUNT_NOT_HIGHER_THAN_PREVIOUS(HttpStatus.BAD_REQUEST, "PAYMENT-008", "상회 입찰 금액이 기존 입찰금보다 높아야 합니다."),
    WALLET_OWNER_MISMATCH(HttpStatus.FORBIDDEN, "PAYMENT-009", "지갑 소유주가 일치하지 않습니다."),
    WALLET_TRANSACTION_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "PAYMENT-010", "구매가 또는 낙찰가가 일치하지 않습니다."),
    WALLET_BUYER_MISMATCH(HttpStatus.FORBIDDEN, "PAYMENT-011", "경매 입찰자가 일치하지 않습니다."),
    WALLET_TRANSACTION_PAYMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "PAYMENT-012", "즉시 구매 정보가 존재하지 않습니다."),
    WALLET_BALANCE_NOT_ZERO(HttpStatus.BAD_REQUEST, "PAYMENT-013", "지갑에 잔액이 남아있어 요청을 처리할 수 없습니다."),
    WALLET_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "PAYMENT-014", "이미 삭제된 지갑입니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}

package com.javaauction.auction_service.presentation.advice;

import com.javaauction.global.infrastructure.code.ResponseCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BidErrorCode implements ResponseCode {

    BID_AUCTION_NOT_FOUND("BID000", "경매를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    BID_FINISHED_AUCTION("BID001", "이미 종료된 경매입니다.", HttpStatus.BAD_REQUEST),
    BID_PRICE_TOO_LOW("BID002", "입찰 금액이 최소 요구 금액보다 적습니다.", HttpStatus.BAD_REQUEST),
    BID_AUCTION_PENDING("BID003", "대기중인 경매입니다.", HttpStatus.BAD_REQUEST),
    BID_AUCTION_INVALID_STATUS("BID004", "입찰이 불가능한 경매 상태입니다.", HttpStatus.BAD_REQUEST),
    BID_ADMIN_NOT_ALLOWED("BID005","관리자는 입찰 불가능합니다.", HttpStatus.FORBIDDEN),
    BID_INSUFFICIENT_BALANCE("BID006", "잔액이 부족하여 입찰할 수 없습니다.", HttpStatus.BAD_REQUEST),
    BID_SAME_USER_CONSECUTIVE_NOT_ALLOWED("BID007", "연속으로 입찰할 수 없습니다.",  HttpStatus.BAD_REQUEST),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;

    BidErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}

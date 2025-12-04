package com.javaauction.auction_service.presentation.advice;

import com.javaauction.global.infrastructure.code.ResponseCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuctionErrorCode implements ResponseCode {
    AUCTION_NOT_FOUND("AUCTION000", "해당 경매를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    AUCTION_ALREADY_EXIST("AUCTION001", "해당 상품에 대한 경매가 존재합니다.", HttpStatus.FOUND),
    AUCTION_END_LATER("AUCTION003", "경매시간은 최소 한시간 이후여야합니다.", HttpStatus.BAD_REQUEST),
    AUCTION_IN_PROGRESS("AUCTION004", "현재 진행중인 경매입니다.", HttpStatus.BAD_REQUEST),
    AUCTION_SUCCESSFUL_BID("AUCTION005", "이미 낙찰된 경매입니다.", HttpStatus.BAD_REQUEST),
    AUCTION_PENDING("AUCTION006", "대기중인 경매입니다.", HttpStatus.BAD_REQUEST),
    AUCTION_NOT_PENDING("AUCTION007", "경매 대기중일때만 수정 가능합니다.", HttpStatus.BAD_REQUEST),
    AUCTION_BUY_NOW_NOT_AVAILABLE("AUCTION008", "즉시 구매가 제공되지 않는 경매입니다.", HttpStatus.BAD_REQUEST),
    AUCTION_PRODUCT_FORBIDDEN("AUCTION009", "본인 상품만 경매를 열 수 있습니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;

    AuctionErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}

package com.javaauction.auction_service.presentation.advice;

import com.javaauction.global.infrastructure.code.ResponseCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuctionErrorCode implements ResponseCode {
    AUCTION_NOT_FOUND("AUCTION000", "해당 경매를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    AUCTION_ALREADY_EXIST("AUCTION001", "해당 상품에 대한 경매가 존재합니다.", HttpStatus.FOUND),
    AUCTION_END_LATER("AUCTION003", "경매시간은 최소 한시간 이후여야합니다.", HttpStatus.BAD_REQUEST);


    private final String code;
    private final String message;
    private final HttpStatus status;

    AuctionErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}

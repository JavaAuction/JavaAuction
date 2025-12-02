package com.javaauction.auction_service.presentation.advice;

import com.javaauction.global.infrastructure.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuctionSuccessCode implements ResponseCode {

    AUCTION_CREATE_SUCCESS(HttpStatus.CREATED, "AUCTION201", "경매 생성 성공"),
    AUCTION_FIND_SUCCESS(HttpStatus.OK, "AUCTION200", "경매 조회 성공"),
    AUCTION_RE_REGISTER_SUCCESS(HttpStatus.ACCEPTED, "AUCTION202", "경매 재등록 성공"),
    AUCTION_DELETED(HttpStatus.NO_CONTENT, "AUCTION204", "경매 삭제 성공"),
    AUCTION_UPDATED(HttpStatus.OK, "AUCTION200", "경매 재등록 성공"),
    AUCTION_STATUS_UPDATED(HttpStatus.OK, "AUCTION200", "경매 상태 변경"),
    AUCTION_BUY_NOW_SUCCESS(HttpStatus.CREATED, "AUCTION201", "즉시 구매 성공");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

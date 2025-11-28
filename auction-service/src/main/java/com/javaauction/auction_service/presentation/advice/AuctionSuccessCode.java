package com.javaauction.auction_service.presentation.advice;

import com.javaauction.global.infrastructure.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuctionSuccessCode implements ResponseCode {

    AUCTION_CREATE_SUCCESS(HttpStatus.CREATED, "AUCTION201", "경매 생성 성공");


    private final HttpStatus status;
    private final String code;
    private final String message;
}

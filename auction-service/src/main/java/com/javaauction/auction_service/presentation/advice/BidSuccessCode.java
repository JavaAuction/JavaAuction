package com.javaauction.auction_service.presentation.advice;

import com.javaauction.global.infrastructure.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BidSuccessCode implements ResponseCode {

    BID_CREATE_SUCCESS(HttpStatus.CREATED, "BID201", "입찰 성공");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
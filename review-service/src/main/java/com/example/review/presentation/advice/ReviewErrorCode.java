package com.example.review.presentation.advice;

import com.javaauction.global.infrastructure.code.ResponseCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReviewErrorCode implements ResponseCode {

    REVIEW_NOT_FOUND("REVIEW-000", "해당 리뷰를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TARGET_NOT_FOUND("REVIEW-001", "대상을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CANNOT_WRITE_OWN_REVIEW("REVIEW-002", "본인의 리뷰는 작성할 수 없습니다.", HttpStatus.CONFLICT),
    AUCTION_NOT_FOUND("REVIEW-003", "해당 옥션을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    AUCTION_NOT_USER_TRANSACTION("REVIEW-004", "해당 옥션은 사용자의 거래가 아닙니다.", HttpStatus.FORBIDDEN),
    REVIEW_ALREADY_EXISTS("REVIEW-005", "해당 옥션에 대한 리뷰가 이미 존재합니다.", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ReviewErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
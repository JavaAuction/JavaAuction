package com.example.review.presentation.advice;

import com.javaauction.global.infrastructure.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewSuccessCode implements ResponseCode {
    REVIEW_CREATED(HttpStatus.CREATED, "USER-201", "리뷰 생성이 완료되었습니다."),
    REVIEW_LIST_FOUND(HttpStatus.OK, "USER-210", "리뷰 목록 조회 성공"),
    REVIEW_FOUND(HttpStatus.OK, "USER-211", "리뷰 조회 성공"),
    REVIEW_UPDATED(HttpStatus.OK, "USER-220", "리뷰 정보 수정 성공"),
    REVIEW_DELETED(HttpStatus.OK, "USER-230", "리뷰 삭제 성공");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

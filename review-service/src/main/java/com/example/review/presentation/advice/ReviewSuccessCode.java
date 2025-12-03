package com.example.review.presentation.advice;

import com.javaauction.global.infrastructure.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewSuccessCode implements ResponseCode {
    REVIEW_CREATED(HttpStatus.CREATED, "REVIEW-201", "리뷰 생성이 완료되었습니다."),
    REVIEW_LIST_FOUND(HttpStatus.OK, "REVIEW-210", "리뷰 목록 조회 성공"),
    REVIEW_FOUND(HttpStatus.OK, "REVIEW-211", "리뷰 조회 성공"),
    REVIEW_UPDATED(HttpStatus.OK, "REVIEW-220", "리뷰 정보 수정 성공"),
    REVIEW_DELETED(HttpStatus.OK, "REVIEW-230", "리뷰 삭제 성공");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

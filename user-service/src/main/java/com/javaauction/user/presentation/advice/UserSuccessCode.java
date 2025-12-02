package com.javaauction.user.presentation.advice;

import com.javaauction.global.infrastructure.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserSuccessCode implements ResponseCode {

    USER_CREATED(HttpStatus.CREATED, "USER-201", "회원가입이 완료되었습니다."),
    LOGIN_SUCCESS(HttpStatus.OK, "USER-200", "로그인 성공"),
    USER_LIST_FOUND(HttpStatus.OK, "USER-210", "유저 목록 조회 성공"),
    USER_FOUND(HttpStatus.OK, "USER-211", "유저 조회 성공"),
    MY_INFO_FOUND(HttpStatus.OK, "USER-212", "내 정보 조회 성공"),
    USER_UPDATED(HttpStatus.OK, "USER-220", "유저 정보 수정 성공"),
    USER_DELETED(HttpStatus.OK, "USER-230", "유저 삭제 성공"),

    ADDRESS_CREATED(HttpStatus.CREATED, "ADDRESS-201", "주소 생성이 완료되었습니다."),
    ADDRESS_LIST_FOUND(HttpStatus.OK,"ADDRESS-210", "주소 목록 조회 성공"),
    ADDRESS_UPDATED(HttpStatus.OK,"ADDRESS-220", "주소 수정 성공"),
    ADDRESS_DELETED(HttpStatus.OK,"ADDRESS-230", "주소 삭제 성공");
    private final HttpStatus status;
    private final String code;
    private final String message;
}


package com.javaauction.chatservice.presentation.dto.advice;

import com.javaauction.global.infrastructure.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatSuccessCode implements ResponseCode {
    CHAT_FIND_SUCCESS(HttpStatus.OK, "ALERT200", "채팅 요청 성공"),
    CHAT_CREATE_SUCCESS(HttpStatus.CREATED, "ALERT201", "채팅 생성 성공");

    private final HttpStatus status;
    private final String code;
    private final String message;

}

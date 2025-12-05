package com.javaauction.chatservice.presentation.advice;

import com.javaauction.global.infrastructure.code.ResponseCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ChatErrorCode implements ResponseCode {

    CHAT_CHATROOM_NOT_FOUND("ALERT000", "해당 채팅방을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CHAT_CHATTING_NOT_FOUND("ALERT001", "해당 채팅을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CHAT_PRODUCT_NOT_FOUND("ALERT002", "해당 상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CHAT_UNAUTH("ALERT003", "자신이 소속된 채팅방의 채팅만 접근 가능합니다.", HttpStatus.UNAUTHORIZED),
    CHAT_CANNOT_CREATE_WITH_SELF("CHAT004", "자신에게 채팅을 요청할 수 없습니다.", HttpStatus.BAD_REQUEST),
    CHAT_CANNOT_CREATE_DIFF_RECEIVER("CHAT005", "채팅방 소속이 아닌 유저에게 메시지를 전송할 수 없습니다.", HttpStatus.BAD_REQUEST),
    CHAT_PRODUCT_FORBIDDEN("CHAT006", "채팅 요청을 보내는 상대가 상품 등록자와 일치하지 않습니다.", HttpStatus.FORBIDDEN),
    CHAT_CHATROOM_ALREADY_EXIST("CHAT009", "해당 상품에 대한 채팅방이 이미 존재합니다.", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ChatErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

}

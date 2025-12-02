package com.javaauction.user.presentation.advice;

import com.javaauction.global.infrastructure.code.ResponseCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements ResponseCode {

    USER_NOT_FOUND("USER-000", "해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USER-001", "이미 존재하는 사용자입니다.", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS("USER-002", "이미 등록된 이메일입니다.", HttpStatus.CONFLICT),
    SLACK_ID_ALREADY_EXISTS("USER-003", "이미 등록된 Slack ID입니다.", HttpStatus.CONFLICT),
    INVALID_LOGIN("USER-004", "아이디 또는 비밀번호가 잘못되었습니다.", HttpStatus.UNAUTHORIZED),
    CANNOT_DELETE_DELETED_USER("USER-005", "이미 삭제된 유저입니다.", HttpStatus.CONFLICT),
    UNAUTHORIZED_USER_ACCESS("USER-006", "해당 유저에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),

    ADDRESS_NOT_FOUND("ADDRESS-000","해당 주소를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ADDRESS_ALREADY_EXISTS("ADDRESS-001","이미 해당 주소가 존재합니다.", HttpStatus.CONFLICT),
    ADDRESS_DEFAULT_DISAPPEAR("ADDRESS-002", "기본 주소가 사라지게 됩니다.", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus status;

    UserErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}


package com.springcloud.eureka.client.productservice.domain.error;

import com.javaauction.global.infrastructure.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ResponseCode {

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROD-001", "존재하지 않는 상품입니다."),
    INVALID_PRODUCT_STATUS(HttpStatus.BAD_REQUEST, "PROD-002", "유효하지 않은 상품 상태입니다."),
    PRODUCT_ALREADY_DELETED(HttpStatus.CONFLICT, "PROD-003", "이미 삭제된 상품입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "PROD-021", "해당 기능을 사용할 권한이 없습니다."),
    CATEGORY_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "PROD-020", "이미 존재하는 카테고리입니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}


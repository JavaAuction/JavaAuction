package com.example.review.presentation.advice;

import com.javaauction.global.infrastructure.code.BaseErrorCode;
import com.javaauction.global.infrastructure.code.ResponseCode;
import com.javaauction.global.presentation.exception.BussinessException;
import com.javaauction.global.presentation.exception.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice(basePackages = {"com.example.review"})
public class ReviewExceptionHandler {

    // BussinessException (도메인 예외 처리)
    @ExceptionHandler(BussinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BussinessException e) {
        log.warn("[BusinessException] {}", e.getMessage());

        return ResponseEntity
                .status(e.getResponseCode().getStatus())
                .body(ErrorResponse.builder()
                        .status(e.getResponseCode().getStatus())
                        .code(e.getResponseCode().getCode())
                        .message(e.getResponseCode().getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // DB 중복 Key 에러 처리
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DataIntegrityViolationException e) {
        String msg = e.getMostSpecificCause().getMessage();
        log.warn("[DB Duplicate Error] {}", msg);

        return build(BaseErrorCode.INVALID_INPUT_VALUE);
    }

    // DTO @Valid 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        log.warn("[Validation Error] {}", e.getMessage());

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .code(BaseErrorCode.INVALID_INPUT_VALUE.getCode())
                        .message(BaseErrorCode.INVALID_INPUT_VALUE.getMessage())
                        .errors(ErrorResponse.FieldError.of(e.getBindingResult()))
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // RequestParam 누락
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException e) {
        return build(BaseErrorCode.MISSING_PARAMETER);
    }

    // JSON Parse 실패
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException e) {
        return build(BaseErrorCode.INVALID_JSON_FORMAT);
    }

    // PathVariable/RequestParam 제약 조건 에러
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException e) {
        return build(BaseErrorCode.INVALID_INPUT_VALUE);
    }

    // 지원하지 않는 HTTP Method
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return build(BaseErrorCode.METHOD_NOT_ALLOWED);
    }

    // 예상하지 못한 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception e) {
        log.error("[Unknown Exception]", e);
        return build(BaseErrorCode.INTERNAL_SERVER_ERROR);
    }

    // ErrorResponse 생성 편의 메서드
    private ResponseEntity<ErrorResponse> build(ResponseCode code) {
        return ResponseEntity
                .status(code.getStatus())
                .body(ErrorResponse.builder()
                        .status(code.getStatus())
                        .code(code.getCode())
                        .message(code.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}


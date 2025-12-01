package com.javaauction.payment_service.presentation.advice;

import com.javaauction.global.presentation.exception.GlobalExceptionHandler;
import com.javaauction.global.presentation.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PaymentExceptionHandler extends GlobalExceptionHandler {

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ApiResponse<?>> handlePaymentException(PaymentException exception) {
        return ResponseEntity.status(exception.getResponseCode().getStatus()).body(ApiResponse.error(exception.getResponseCode()));
    }
}

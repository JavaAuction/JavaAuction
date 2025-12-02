package com.javaauction.chatservice.presentation.dto.advice;

import com.javaauction.global.presentation.exception.GlobalExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler extends GlobalExceptionHandler {
}

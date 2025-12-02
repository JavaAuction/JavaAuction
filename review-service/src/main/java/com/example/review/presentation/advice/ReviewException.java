package com.example.review.presentation.advice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewException extends RuntimeException {
    private final ReviewErrorCode reviewErrorCode;
}

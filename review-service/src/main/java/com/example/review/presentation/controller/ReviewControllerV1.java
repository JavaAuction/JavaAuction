package com.example.review.presentation.controller;

import com.example.review.application.dto.ReqCreateReviewDto;
import com.example.review.application.service.ReviewServiceV1;
import com.example.review.infrastructure.JWT.JwtUserContext;
import com.example.review.presentation.advice.ReviewSuccessCode;
import com.javaauction.global.presentation.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("v1/reviews")
@RequiredArgsConstructor
public class ReviewControllerV1 {
    private final ReviewServiceV1 reviewService;

    @PostMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> createReview(@PathVariable String userId, @RequestBody ReqCreateReviewDto reqCreateReviewDto) {
        reviewService.createReview(userId, JwtUserContext.getUsernameFromHeader(), reqCreateReviewDto);
        return ResponseEntity.ok(ApiResponse.success(ReviewSuccessCode.REVIEW_CREATED));
    }
}

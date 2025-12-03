package com.example.review.presentation.controller;

import com.example.review.application.dto.ReqCreateReviewDto;
import com.example.review.application.dto.ReqUpdateReviewDto;
import com.example.review.application.dto.ResGetReviewDto;
import com.example.review.application.service.ReviewServiceV1;
import com.example.review.infrastructure.JWT.JwtUserContext;
import com.example.review.presentation.advice.ReviewSuccessCode;
import com.javaauction.global.presentation.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @GetMapping()
    public ResponseEntity<ApiResponse<Page<ResGetReviewDto>>> getReviews(@RequestParam(value = "page", defaultValue = "1") int page,
                                                                         @RequestParam(value = "size", defaultValue = "10") int size,
                                                                         @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
                                                                         @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {
        return ResponseEntity.ok(ApiResponse.success(ReviewSuccessCode.REVIEW_LIST_FOUND, reviewService.getReviews(page - 1, size, sortBy, isAsc,JwtUserContext.getRoleFromHeader())));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<ResGetReviewDto>>> getUserReviews(@PathVariable String userId,
                                                                             @RequestParam(value = "page", defaultValue = "1") int page,
                                                                             @RequestParam(value = "size", defaultValue = "10") int size,
                                                                             @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
                                                                             @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc,
                                                                             @RequestParam(value = "isWriter", defaultValue = "false")  boolean isWriter) {
        return ResponseEntity.ok(ApiResponse.success(ReviewSuccessCode.REVIEW_LIST_FOUND, reviewService.getUserReviews(userId,page - 1, size, sortBy, isAsc, isWriter)));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> updateReview(@PathVariable UUID reviewId, @RequestBody ReqUpdateReviewDto reqUpdateReviewDto) {
        reviewService.updateReview(reviewId, reqUpdateReviewDto, JwtUserContext.getUsernameFromHeader());
        return ResponseEntity.ok(ApiResponse.success(ReviewSuccessCode.REVIEW_UPDATED));
    }
}

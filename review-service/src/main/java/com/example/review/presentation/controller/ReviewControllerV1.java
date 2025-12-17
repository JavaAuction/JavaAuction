package com.example.review.presentation.controller;

import com.example.review.application.dto.ReqCreateReviewDto;
import com.example.review.application.dto.ReqUpdateReviewDto;
import com.example.review.presentation.dto.ResGetReviewDto;
import com.example.review.application.service.ReviewServiceV1;
import com.example.review.infrastructure.JWT.JwtUserContext;
import com.example.review.presentation.advice.ReviewSuccessCode;
import com.javaauction.global.presentation.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("v1/reviews")
@RequiredArgsConstructor
@Tag(name = "사용자 리뷰 API", description = "사용자 리뷰 관련 API입니다.")
public class ReviewControllerV1 {
    private final ReviewServiceV1 reviewService;

    @Operation(summary = "리뷰 생성",description = "리뷰를 생성합니다.")
    @PostMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> createReview(@PathVariable String userId, @RequestBody ReqCreateReviewDto reqCreateReviewDto) {
        reviewService.createReview(userId, JwtUserContext.getUsernameFromHeader(), reqCreateReviewDto);
        return ResponseEntity.ok(ApiResponse.success(ReviewSuccessCode.REVIEW_CREATED));
    }

    @Operation(summary = "리뷰 조회",description = "전체 리뷰 목록을 조회합니다.(관리자만 가능)")
    @GetMapping()
    public ResponseEntity<ApiResponse<Page<ResGetReviewDto>>> getReviews(@RequestParam(value = "page", defaultValue = "1") int page,
                                                                         @RequestParam(value = "size", defaultValue = "10") int size,
                                                                         @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
                                                                         @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {
        return ResponseEntity.ok(ApiResponse.success(ReviewSuccessCode.REVIEW_LIST_FOUND, reviewService.getReviews(page - 1, size, sortBy, isAsc,JwtUserContext.getRoleFromHeader())));
    }

    @Operation(summary = "특정 사용자 리뷰 조회",description = "특정 사용자에 대한 리뷰 목록을 조회합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<ResGetReviewDto>>> getUserReviews(@PathVariable String userId,
                                                                             @RequestParam(value = "page", defaultValue = "1") int page,
                                                                             @RequestParam(value = "size", defaultValue = "10") int size,
                                                                             @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
                                                                             @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc,
                                                                             @RequestParam(value = "isWriter", defaultValue = "false")  boolean isWriter) {
        return ResponseEntity.ok(ApiResponse.success(ReviewSuccessCode.REVIEW_LIST_FOUND, reviewService.getUserReviews(userId,page - 1, size, sortBy, isAsc, isWriter)));
    }

    @Operation(summary = "리뷰 수정",description = "리뷰를 수정합니다.")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> updateReview(@PathVariable UUID reviewId, @RequestBody ReqUpdateReviewDto reqUpdateReviewDto) {
        reviewService.updateReview(reviewId, reqUpdateReviewDto, JwtUserContext.getUsernameFromHeader());
        return ResponseEntity.ok(ApiResponse.success(ReviewSuccessCode.REVIEW_UPDATED));
    }

    @Operation(summary = "리뷰 삭제",description = "리뷰를 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable UUID reviewId) {
        reviewService.deleteReview(reviewId, JwtUserContext.getUsernameFromHeader(), JwtUserContext.getRoleFromHeader());
        return ResponseEntity.ok(ApiResponse.success(ReviewSuccessCode.REVIEW_DELETED));
    }
}

package com.example.review.presentation.controller;

import com.example.review.application.service.ReviewServiceV1;
import com.example.review.infrastructure.JWT.JwtUserContext;
import com.example.review.presentation.dto.ResGetReviewDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/reviews")
@RequiredArgsConstructor
@Tag(name = "내부 통신용 사용자 리뷰 API", description = "내부 서비스용 사용자 리뷰 관련 API입니다.")
public class ReviewInternalController {

    private final ReviewServiceV1 reviewService;

    @Operation(summary = "사용자 리뷰 조회",description = "해당 사용자에 대한 리뷰들을 조회합니다.")
    @GetMapping("/user/{userId}")
    public List<ResGetReviewDto> getReviewByUserId(@PathVariable String userId) {
        return reviewService.getUserReviewList(userId);
    }

    @Operation(summary = "사용자 평점 조회",description = "사용자의 평점을 조회합니다.")
    @GetMapping("/user/{userId}/rating")
    public double getUserRating(@PathVariable String userId) {
        return reviewService.getAverageRatingByTarget(userId);
    }

    @Operation(summary = "사용자 리뷰 삭제",description = "해당 사용자와 관련된 리뷰를 삭제합니다.")
    @DeleteMapping("/user/{userId}")
    public void deleteAllByUserId(@PathVariable String userId) {
        reviewService.deleteAllByUserId(userId, JwtUserContext.getUsernameFromHeader());
    }
}

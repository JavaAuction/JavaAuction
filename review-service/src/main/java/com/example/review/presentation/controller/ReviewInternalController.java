package com.example.review.presentation.controller;

import com.example.review.application.service.ReviewServiceV1;
import com.example.review.infrastructure.JWT.JwtUserContext;
import com.example.review.presentation.dto.ResGetReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/reviews")
@RequiredArgsConstructor
public class ReviewInternalController {

    private final ReviewServiceV1 reviewService;

    @GetMapping("/user/{userId}")
    public List<ResGetReviewDto> getReviewByUserId(@PathVariable String userId) {
        return reviewService.getUserReviewList(userId);
    }

    @GetMapping("/user/{userId}/rating")
    public double getUserRating(@PathVariable String userId) {
        return reviewService.getAverageRatingByTarget(userId);
    }

    @DeleteMapping("/user/{userId}")
    public void deleteAllByUserId(@PathVariable String userId) {
        reviewService.deleteAllByUserId(userId, JwtUserContext.getUsernameFromHeader());
    }
}

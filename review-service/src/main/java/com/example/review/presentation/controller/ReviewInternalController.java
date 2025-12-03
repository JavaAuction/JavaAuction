package com.example.review.presentation.controller;

import com.example.review.application.service.ReviewServiceV1;
import com.example.review.presentation.dto.ResGetReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

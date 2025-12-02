package com.example.review.application.service;

import com.example.review.application.dto.ReqCreateReviewDto;
import com.example.review.domain.entity.ReviewEntity;
import com.example.review.domain.repository.ReviewRepository;
import com.example.review.infrastructure.feign.client.UserServiceClient;
import com.example.review.infrastructure.feign.dto.ResGetUserIntDto;
import com.example.review.presentation.advice.ReviewErrorCode;
import com.javaauction.global.presentation.exception.BussinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceV1 {
    private final ReviewRepository reviewRepository;
    private final UserServiceClient userServiceClient;

    public void createReview(String userId, String username, ReqCreateReviewDto reqCreateReviewDto) {
        if(!userServiceClient.existsUser(userId)) {
            throw new BussinessException(ReviewErrorCode.TARGET_NOT_FOUND);
        }
        //본인의 리뷰 작성 불가
        if (userId.equals(username)) {
            throw new BussinessException(ReviewErrorCode.CANNOT_WRITE_OWN_REVIEW);
        }

        //옥션 입찰자인지 확인 절차(추후 연결)

        ReviewEntity review = ReviewEntity.builder()
                .rating(reqCreateReviewDto.getRating())
                .content(reqCreateReviewDto.getContent())
                .writer(username)
                .target(userId)
                .build();

        review.setCreate(Instant.now(),username);

        reviewRepository.save(review);
    }
}

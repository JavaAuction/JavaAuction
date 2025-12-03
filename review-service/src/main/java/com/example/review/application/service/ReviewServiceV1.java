package com.example.review.application.service;

import com.example.review.application.dto.ReqCreateReviewDto;
import com.example.review.application.dto.ResGetReviewDto;
import com.example.review.domain.entity.ReviewEntity;
import com.example.review.domain.repository.ReviewRepository;
import com.example.review.infrastructure.feign.client.UserServiceClient;
import com.example.review.presentation.advice.ReviewErrorCode;
import com.javaauction.global.infrastructure.code.BaseErrorCode;
import com.javaauction.global.presentation.exception.BussinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

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

    @Transactional(readOnly = true)
    public Page<ResGetReviewDto> getReviews(int page, int size, String sortBy, boolean isAsc, String roleFromHeader) {
        if (!"ADMIN".equals(roleFromHeader)) {
            throw new BussinessException(BaseErrorCode.ACCESS_DENIED);
        }

        Pageable pageable = buildPageable(page, size, sortBy, isAsc);

        return reviewRepository.findAll(pageable)
                .map(ResGetReviewDto::of);
    }

    @Transactional(readOnly = true)
    public Page<ResGetReviewDto> getUserReviews(String userId, int page, int size, String sortBy, boolean isAsc, boolean isWriter) {
        Pageable pageable = buildPageable(page, size, sortBy, isAsc);
        if (isWriter) {
            return reviewRepository.findByWriter(userId, pageable).map(ResGetReviewDto::of);
        }
        return  reviewRepository.findByTarget(userId,pageable).map(ResGetReviewDto::of);
    }



    private Pageable buildPageable(int page, int size, String sortBy, boolean isAsc) {
        int fixedSize = (size == 10 || size == 30 || size == 50) ? size : 10;
        String fixedSort = (sortBy != null && !sortBy.isBlank() && sortBy.equals("modifiedAt")) ? "modifiedAt" : "createdAt";

        return PageRequest.of(
                page > 0 ? page - 1 : 0,
                fixedSize,
                isAsc ? Sort.Direction.ASC : Sort.Direction.DESC,
                fixedSort
        );
    }

}

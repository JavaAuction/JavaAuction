package com.example.review.application.service;

import com.example.review.application.dto.ReqCreateReviewDto;
import com.example.review.application.dto.ReqUpdateReviewDto;
import com.example.review.presentation.dto.ResGetReviewDto;
import com.example.review.domain.entity.ReviewEntity;
import com.example.review.domain.repository.ReviewRepository;
import com.example.review.infrastructure.kafka.UserEventService;
import com.example.review.infrastructure.kafka.AuctionEventService;
import com.example.review.infrastructure.event.AuctionValidationResponseEvent;
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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceV1 {
    private final ReviewRepository reviewRepository;
    private final UserEventService userEventService;
    private final AuctionEventService auctionEventService;

    public void createReview(String userId, String username, ReqCreateReviewDto reqCreateReviewDto) {
        if(!userEventService.existsUser(userId)) {
            throw new BussinessException(ReviewErrorCode.TARGET_NOT_FOUND);
        }
        //본인의 리뷰 작성 불가
        if (userId.equals(username)) {
            throw new BussinessException(ReviewErrorCode.CANNOT_WRITE_OWN_REVIEW);
        }

        // auctionId 검증
        UUID auctionId = reqCreateReviewDto.getAuctionId();
        if (auctionId == null) {
            throw new BussinessException(ReviewErrorCode.AUCTION_NOT_FOUND);
        }

        // 해당 auctionId로 이미 작성된 리뷰가 있는지 확인
        if (reviewRepository.findByAuctionIdAndDeletedAtIsNull(auctionId).isPresent()) {
            throw new BussinessException(ReviewErrorCode.REVIEW_ALREADY_EXISTS);
        }

        // auction이 사용자의 거래인지 확인 (auction-service로 이벤트 발행)
        AuctionValidationResponseEvent validationResponse = auctionEventService.validateAuction(auctionId, username);
        if (!validationResponse.isValid()) {
            throw new BussinessException(ReviewErrorCode.AUCTION_NOT_USER_TRANSACTION);
        }

        // seller 또는 buyer인지 확인
        // seller는 buyer에게, buyer는 seller에게 리뷰를 작성할 수 있음
        boolean isSeller = validationResponse.getSellerId().equals(username);
        boolean isBuyer = validationResponse.getBuyerId() != null && validationResponse.getBuyerId().equals(username);
        
        if (isSeller && !validationResponse.getBuyerId().equals(userId)) {
            throw new BussinessException(ReviewErrorCode.AUCTION_NOT_USER_TRANSACTION);
        }
        if (isBuyer && !validationResponse.getSellerId().equals(userId)) {
            throw new BussinessException(ReviewErrorCode.AUCTION_NOT_USER_TRANSACTION);
        }
        if (!isSeller && !isBuyer) {
            throw new BussinessException(ReviewErrorCode.AUCTION_NOT_USER_TRANSACTION);
        }

        ReviewEntity review = ReviewEntity.builder()
                .rating(reqCreateReviewDto.getRating())
                .content(reqCreateReviewDto.getContent())
                .auctionId(auctionId)
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
        if (!userEventService.existsUser(userId)) {
            throw new BussinessException(ReviewErrorCode.TARGET_NOT_FOUND);
        }
        Pageable pageable = buildPageable(page, size, sortBy, isAsc);
        if (isWriter) {
            return reviewRepository.findByWriter(userId, pageable).map(ResGetReviewDto::of);
        }
        return  reviewRepository.findByTarget(userId,pageable).map(ResGetReviewDto::of);
    }

    @Transactional
    public void updateReview(UUID reviewId, ReqUpdateReviewDto reqUpdateReviewDto, String usernameFromHeader) {
        ReviewEntity review = reviewRepository.findById(reviewId).orElseThrow(() -> new BussinessException(ReviewErrorCode.REVIEW_NOT_FOUND));

        if (!review.getWriter().equals(usernameFromHeader)) {
            throw new BussinessException(BaseErrorCode.ACCESS_DENIED);
        }

        review.update(reqUpdateReviewDto);
        review.setUpdated(Instant.now(),usernameFromHeader);
    }

    @Transactional
    public void deleteReview(UUID reviewId, String usernameFromHeader, String roleFromHeader) {
        ReviewEntity review = reviewRepository.findById(reviewId).orElseThrow(() -> new BussinessException(ReviewErrorCode.REVIEW_NOT_FOUND));

        if (!review.getWriter().equals(usernameFromHeader) && !"ADMIN".equals(roleFromHeader)) {
            throw new BussinessException(BaseErrorCode.ACCESS_DENIED);
        }

        review.softDelete(Instant.now(),usernameFromHeader);
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


    //internal api
    @Transactional(readOnly = true)
    public List<ResGetReviewDto> getUserReviewList(String userId) {
        List<ReviewEntity> reviews = reviewRepository.findByTarget(userId);
        return reviews.stream()
                .map(ResGetReviewDto::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public double getAverageRatingByTarget(String userId) {
        Double averageRating = reviewRepository.calculateAverageRatingByTarget(userId);
        return averageRating != null ? averageRating : 0.0;
    }

    @Transactional
    public void deleteAllByUserId(String userId, String usernameFromHeader) {
        List<ReviewEntity> getReviews = reviewRepository.findByTarget(userId);
        List<ReviewEntity> writeReviews = reviewRepository.findByWriter(userId);

        getReviews.forEach(review -> {review.softDelete(Instant.now(),usernameFromHeader);});
        writeReviews.forEach(review -> {review.softDelete(Instant.now(),usernameFromHeader);});
    }
}

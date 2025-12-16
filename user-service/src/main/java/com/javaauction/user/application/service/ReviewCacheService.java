package com.javaauction.user.application.service;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.javaauction.user.infrastructure.external.dto.GetReviewIntDto;
import com.javaauction.user.infrastructure.external.kafka.ReviewEventService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewCacheService {

    private final ReviewEventService reviewEventService;

    /**
     * 리뷰 목록 캐싱
     */
    @Cacheable(value = "review", key = "'reviews_' + #userId", unless = "#result == null")
    public List<GetReviewIntDto> getCachedReviews(String userId) {
        log.warn("❌ CACHE MISS → 리뷰 목록 Kafka 조회: userId={}", userId);
        return reviewEventService.getReviewByUser(userId);
    }

    /**
     * 평점 캐싱 (별도 캐싱으로 더 효율적)
     */
    @Cacheable(value = "review", key = "'rating_' + #userId", unless = "#result == null")
    public double getCachedRating(String userId) {
        log.warn("❌ CACHE MISS → 평점 Kafka 조회: userId={}", userId);
        double rating = reviewEventService.getUserRating(userId);
        return Math.round(rating * 10) / 10.0;
    }

    /**
     * 리뷰 정보 조회 (reviews + rating, 각각 캐싱됨)
     * 주의: 같은 클래스 내부 호출 시 캐싱이 작동하지 않으므로
     * UserServiceV1에서 getCachedReviews()와 getCachedRating()을 직접 호출해야 함
     */
    public ReviewInfo getCachedReviewInfo(String userId) {
        // 같은 클래스 내부 호출이므로 캐싱이 작동하지 않음
        // UserServiceV1에서 직접 호출하도록 변경 필요
        List<GetReviewIntDto> reviews = getCachedReviews(userId);
        double rating = getCachedRating(userId);
        return new ReviewInfo(reviews, rating);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    public static class ReviewInfo {
        private List<GetReviewIntDto> reviews;
        private double rating;
    }
}

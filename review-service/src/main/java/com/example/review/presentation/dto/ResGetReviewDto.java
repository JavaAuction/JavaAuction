package com.example.review.presentation.dto;

import com.example.review.domain.entity.ReviewEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResGetReviewDto {
    private String writer;
    private String target;
    private double rating;
    private String content;

    public static ResGetReviewDto of(ReviewEntity reviewEntity) {
        return ResGetReviewDto.builder()
                .writer(reviewEntity.getWriter())
                .target(reviewEntity.getTarget())
                .rating(reviewEntity.getRating())
                .content(reviewEntity.getContent())
                .build();
    }
}

package com.example.review.domain.entity;

import com.example.review.application.dto.ReqCreateReviewDto;
import com.example.review.application.dto.ReqUpdateReviewDto;
import com.javaauction.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_review")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private double rating;

//    @Column(nullable = false, unique = true)
//    private UUID auctionId;

    @Column(nullable = false)
    private String writer;

    @Column(nullable = false)
    private String target;

    @Column(nullable = false)
    private String content;

    public void update(ReqUpdateReviewDto reqUpdateReviewDto) {
        this.rating = reqUpdateReviewDto.getRating();
        this.content = reqUpdateReviewDto.getContent();
    }
}

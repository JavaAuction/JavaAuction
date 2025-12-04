package com.example.review.domain.repository;

import com.example.review.domain.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository {
    ReviewEntity save(ReviewEntity review);
    Optional<ReviewEntity> findById(UUID id);
    Page<ReviewEntity> findByTarget(String target, Pageable pageable);
    Page<ReviewEntity> findByWriter(String writer, Pageable pageable);
    Page<ReviewEntity> findAll(Pageable pageable);
    List<ReviewEntity> findByTarget(String target);
    List<ReviewEntity> findByWriter(String writer);
    Double calculateAverageRatingByTarget(String target);
}

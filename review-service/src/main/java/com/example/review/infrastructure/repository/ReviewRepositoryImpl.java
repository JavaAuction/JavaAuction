package com.example.review.infrastructure.repository;

import com.example.review.domain.entity.ReviewEntity;
import com.example.review.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {

    private final ReviewJpaRepository reviewjpaRepository;

    @Override
    public ReviewEntity save(ReviewEntity review) {
        return reviewjpaRepository.save(review);
    }

    @Override
    public Optional<ReviewEntity> findById(UUID id) {
        return reviewjpaRepository.findById(id);
    }

    @Override
    public Page<ReviewEntity> findByTarget(String target, Pageable pageable) {
        return reviewjpaRepository.findByTarget(target, pageable);
    }

    @Override
    public Page<ReviewEntity> findByWriter(String writer, Pageable pageable) {
        return reviewjpaRepository.findByWriter(writer, pageable);
    }
}

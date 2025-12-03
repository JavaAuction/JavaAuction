package com.example.review.infrastructure.repository;

import com.example.review.domain.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewJpaRepository extends JpaRepository<ReviewEntity, UUID> {
    Page<ReviewEntity> findByTarget(String target, Pageable pageable);

    Page<ReviewEntity> findByWriter(String writer, Pageable pageable);
    
    List<ReviewEntity> findByTarget(String target);
    
    @org.springframework.data.jpa.repository.Query("SELECT AVG(r.rating) FROM ReviewEntity r WHERE r.target = :target")
    Double calculateAverageRatingByTarget(String target);
}

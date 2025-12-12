package com.example.review.infrastructure.kafka;

import com.example.review.application.service.ReviewServiceV1;
import com.example.review.infrastructure.event.ReviewGetResponseEvent;
import com.example.review.infrastructure.event.RatingGetResponseEvent;
import com.example.review.presentation.dto.ResGetReviewDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewEventConsumer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ReviewServiceV1 reviewServiceV1;

    @KafkaListener(topics = "review.get.request", groupId = "review-service-group")
    public void consumeReviewGetRequest(Map<String, Object> payload, Acknowledgment acknowledgment) {
        try {
            String userId = (String) payload.get("userId");
            String correlationId = (String) payload.get("correlationId");
            
            log.info("Received review get request: userId={}, correlationId={}", userId, correlationId);
            
            List<ResGetReviewDto> reviews = reviewServiceV1.getUserReviewList(userId);
            
            List<com.example.review.infrastructure.dto.GetReviewIntDto> reviewDtos = reviews.stream()
                    .map(r -> com.example.review.infrastructure.dto.GetReviewIntDto.builder()
                            .writer(r.getWriter())
                            .target(r.getTarget())
                            .rating(r.getRating())
                            .content(r.getContent())
                            .build())
                    .collect(Collectors.toList());
            
            ReviewGetResponseEvent responseEvent = new ReviewGetResponseEvent(
                    correlationId,
                    reviewDtos
            );
            
            log.info("Sending review get response: correlationId={}", correlationId);
            kafkaTemplate.send("review.get.response", correlationId, responseEvent);
        } catch (Exception e) {
            log.error("Error processing review get request", e);
        }
        
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "review.rating.get.request", groupId = "review-service-group")
    public void consumeRatingGetRequest(Map<String, Object> payload, Acknowledgment acknowledgment) {
        try {
            String userId = (String) payload.get("userId");
            String correlationId = (String) payload.get("correlationId");
            
            log.info("Received rating get request: userId={}, correlationId={}", userId, correlationId);
            
            double rating = reviewServiceV1.getAverageRatingByTarget(userId);
            
            RatingGetResponseEvent responseEvent = new RatingGetResponseEvent(
                    correlationId,
                    rating
            );
            
            log.info("Sending rating get response: correlationId={}, rating={}", correlationId, rating);
            kafkaTemplate.send("review.rating.get.response", correlationId, responseEvent);
        } catch (Exception e) {
            log.error("Error processing rating get request", e);
        }
        
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "review.delete.request", groupId = "review-service-group")
    public void consumeReviewDeleteRequest(Map<String, Object> payload, Acknowledgment acknowledgment) {
        try {
            String userId = (String) payload.get("userId");
            
            log.info("Received review delete request: userId={}", userId);
            
            reviewServiceV1.deleteAllByUserId(userId, "system");
            log.info("Successfully deleted reviews for user: userId={}", userId);
        } catch (Exception e) {
            log.error("Error processing review delete request", e);
        }
        
        acknowledgment.acknowledge();
    }
}


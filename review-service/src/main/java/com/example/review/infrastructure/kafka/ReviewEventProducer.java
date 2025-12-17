package com.example.review.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 리뷰 변경 이벤트 발행 (생성/수정/삭제)
     * user-service에서 리뷰 캐시를 무효화하기 위해 사용
     */
    public void publishReviewChanged(String writer, String target) {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", writer);  // 리뷰를 작성한 사용자
        event.put("targetUserId", target);  // 리뷰를 받은 사용자
        
        log.info("리뷰 변경 이벤트 발행: writer={}, target={}", writer, target);
        kafkaTemplate.send("review.changed", writer, event);
    }
}


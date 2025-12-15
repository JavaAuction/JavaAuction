package com.javaauction.auction_service.application.event;

import com.javaauction.auction_service.application.service.BidService;
import com.javaauction.auction_service.domain.event.BidGetRequestEvent;
import com.javaauction.auction_service.domain.event.BidGetResponseEvent;
import com.javaauction.auction_service.presentation.dto.response.internal.ResInternalBidsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BidGetRequestConsumer {

    private final BidService bidService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(
            topics = "bid.get.request",
            groupId = "auction-service-group"
    )
    public void consume(BidGetRequestEvent event) {

        String userId = event.getUserId();
        String correlationId = event.getCorrelationId();

        log.info("[BidGetRequestConsumer] userId={}, correlationId={}",
                userId, correlationId);

        ResInternalBidsDto result =
                bidService.internalGetBids(userId);

        BidGetResponseEvent responseEvent =
                new BidGetResponseEvent(correlationId, result);

        // response 이벤트 발행
        kafkaTemplate.send(
                "bid.get.response",
                correlationId,
                responseEvent
        );
    }
}


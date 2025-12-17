package com.javaauction.auction_service.application.event;

import com.javaauction.auction_service.application.service.BidService;
import com.javaauction.auction_service.domain.entity.enums.BidStatus;
import com.javaauction.auction_service.domain.event.BidGetRequestEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public void consume(BidGetRequestEvent event, Acknowledgment ack) {

        String userId = event.getUserId();
        String correlationId = event.getCorrelationId();

        log.info(
                "[auction] BidGetRequest 수신완료. userId={}, correlationId={}",
                userId, correlationId
        );

        var result = bidService.internalGetBids(userId);

        // Kafka 전용 payload로 변환 (Instant → String)
        List<BidPayload> bidPayloads = result.getBids().stream()
                .map(bid -> new BidPayload(
                        bid.getBidId(),
                        bid.getAuctionId(),
                        bid.getProductId(),
                        bid.getBidPrice(),
                        bid.getStatus(),
                        bid.getCreatedAt() != null
                                ? bid.getCreatedAt().toString()
                                : null
                ))
                .collect(Collectors.toList());

        BidGetResponsePayload responsePayload =
                new BidGetResponsePayload(
                        correlationId,
                        new BidsWrapper(result.getUserId(), bidPayloads)
                );

        ack.acknowledge();
        try {
            // response 이벤트 발행
            kafkaTemplate.send(
                    "bid.get.response",
                    correlationId,
                    responsePayload
            );
            ack.acknowledge();
        } catch (Exception e) {
            log.error("[auction] bid.get.response 발행 실패", e);
        }
    }

    /* ================= Kafka Response Payload ================= */

    @Getter
    @AllArgsConstructor
    static class BidGetResponsePayload {
        private String correlationId;
        private BidsWrapper bids;
    }

    @Getter
    @AllArgsConstructor
    static class BidsWrapper {
        private String userId;
        private List<BidPayload> bids;
    }

    @Getter
    @AllArgsConstructor
    static class BidPayload {
        private UUID bidId;
        private UUID auctionId;
        private UUID productId;
        private Long bidPrice;
        private BidStatus status;
        private String createdAt;
    }
}
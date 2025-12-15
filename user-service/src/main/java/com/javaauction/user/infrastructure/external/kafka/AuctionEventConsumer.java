package com.javaauction.user.infrastructure.external.kafka;

import com.javaauction.user.infrastructure.external.dto.InternalBidDto;
import com.javaauction.user.infrastructure.external.dto.ResInternalBidsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionEventConsumer {

    private final Map<String, CompletableFuture<Object>> pendingRequests = new ConcurrentHashMap<>();

    @KafkaListener(topics = "bid.get.response", groupId = "user-service-group")
    public void consumeBidGetResponse(Map<String, Object> payload, Acknowledgment acknowledgment) {
        try {
            String correlationId = (String) payload.get("correlationId");
            Map<String, Object> bidsMap = (Map<String, Object>) payload.get("bids");
            
            log.info("Received bid get response: correlationId={}", correlationId);
            
            String userId = (String) bidsMap.get("userId");
            List<Map<String, Object>> bidList = (List<Map<String, Object>>) bidsMap.get("bids");
            
            List<InternalBidDto> bids = bidList.stream()
                    .map(bidMap -> {
                        Object statusObj = bidMap.get("status");
                        String status = statusObj != null ? statusObj.toString() : null;
                        
                        UUID bidId = null;
                        UUID auctionId = null;
                        UUID productId = null;
                        Long bidPrice = null;
                        java.time.Instant createdAt = null;
                        
                        try {
                            if (bidMap.get("bidId") != null) {
                                bidId = UUID.fromString(bidMap.get("bidId").toString());
                            }
                            if (bidMap.get("auctionId") != null) {
                                auctionId = UUID.fromString(bidMap.get("auctionId").toString());
                            }
                            if (bidMap.get("productId") != null) {
                                productId = UUID.fromString(bidMap.get("productId").toString());
                            }
                            if (bidMap.get("bidPrice") != null) {
                                bidPrice = ((Number) bidMap.get("bidPrice")).longValue();
                            }
                            if (bidMap.get("createdAt") != null) {
                                Object createdAtObj = bidMap.get("createdAt");
                                if (createdAtObj instanceof String) {
                                    createdAt = java.time.Instant.parse((String) createdAtObj);
                                } else if (createdAtObj instanceof Number) {
                                    createdAt = java.time.Instant.ofEpochMilli(((Number) createdAtObj).longValue());
                                }
                            }
                        } catch (Exception e) {
                            log.warn("Error parsing bid field: {}", e.getMessage());
                        }
                        
                        return InternalBidDto.builder()
                                .bidId(bidId)
                                .auctionId(auctionId)
                                .productId(productId)
                                .bidPrice(bidPrice)
                                .status(status)
                                .createdAt(createdAt)
                                .build();
                    })
                    .collect(Collectors.toList());
            
            ResInternalBidsDto bidsDto = ResInternalBidsDto.builder()
                    .userId(userId)
                    .bids(bids)
                    .build();
            
            CompletableFuture<Object> future = pendingRequests.remove(correlationId);
            if (future != null) {
                future.complete(bidsDto);
            }
        } catch (Exception e) {
            log.error("Error processing bid get response", e);
        }
        
        acknowledgment.acknowledge();
    }

    public CompletableFuture<Object> waitForResponse(String correlationId) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);
        return future;
    }
}


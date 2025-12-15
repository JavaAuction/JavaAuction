package com.javaauction.auction_service.application.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.entity.Bid;
import com.javaauction.auction_service.domain.entity.enums.BidStatus;
import com.javaauction.auction_service.domain.event.BidAlertEvent;
import com.javaauction.auction_service.infrastructure.client.dto.AlertType;
import com.javaauction.auction_service.infrastructure.repository.AuctionRepository;
import com.javaauction.auction_service.infrastructure.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletDeductResultConsumer {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final ApplicationEventPublisher eventPublisher;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "wallet.deduct.result", groupId = "auction-service")
    @Transactional
    public void onMessage(ConsumerRecord<String, String> record) {
        String typeId = getTypeId(record);
        String json = record.value();

        log.info("[auction] wallet.deduct.result 수신 typeId={}, key={}, offset={}, value={}",
                typeId, record.key(), record.offset(), json);

        try {
            JsonNode root = objectMapper.readTree(json);

            UUID auctionId = uuid(root, "auctionId");
            UUID bidId = uuid(root, "bidId");
            if (auctionId == null || bidId == null) {
                log.warn("[auction] 필수값 누락(auctionId/bidId). typeId={}, json={}", typeId, json);
                return;
            }

            if (typeId != null && typeId.contains("WalletDeductFailedEvent")) {
                handleFail(bidId);
                return;
            }

            if (typeId != null && typeId.contains("WalletDeductSucceededEvent")) {
                handleSuccess(auctionId, bidId);
                return;
            }

            if (root.has("errorCode") || root.has("errorMessage")) {
                handleFail(bidId);
                return;
            }

            log.warn("[auction] 알 수 없는 타입. typeId={}, json={}", typeId, json);

        } catch (Exception e) {
            log.error("[auction] 이벤트 처리 실패. typeId={}, offset={}, json={}", typeId, record.offset(), json, e);
        }
    }

    private void handleSuccess(UUID auctionId, UUID bidId) {
        Bid currentBid = bidRepository.findById(bidId).orElse(null);
        if (currentBid == null) {
            log.warn("[auction] 성공 이벤트 수신 → bid 없음. 중복/지연 이벤트 또는 이미 처리된 bid로 판단하여 무시. bidId={}", bidId);
            return;
        }

        // 이미 처리된 이벤트(중복/재전송) 방어
        if (currentBid.getStatus() != BidStatus.PENDING) {
            log.info("[auction] 중복 성공 이벤트 무시. bidId={}, status={}", bidId, currentBid.getStatus());
            return;
        }

        Auction auction = auctionRepository.findById(auctionId).orElse(null);
        if (auction == null) {
            log.warn("[auction] 성공 이벤트 수신 → auction 없음. 이미 종료/삭제되었거나 지연 이벤트로 판단하여 무시. auctionId={}", auctionId);
            return;
        }

        Long cur = auction.getCurrentPrice();
        long currentPrice = (cur != null) ? cur : auction.getStartPrice();

        // 최종 1개만 HELD
        // 현재 price보다 크지 않으면 실패 처리
        if (currentBid.getBidPrice() <= currentPrice) {
            currentBid.markFailed();
            bidRepository.saveAndFlush(currentBid);
            return;
        }

        // 이전 HELD RELEASE
        Bid prevHeld = bidRepository
                .findTopByAuctionIdAndStatusOrderByBidPriceDesc(auctionId, BidStatus.HELD);

        if (prevHeld != null) {
            prevHeld.markReleased();
            bidRepository.save(prevHeld);
        }

        // HELD 확정
        currentBid.markHeld();
        auction.updateCurrentPrice(currentBid.getBidPrice());

        bidRepository.save(currentBid);
        auctionRepository.save(auction);

        bidRepository.flush();
        auctionRepository.flush();

        eventPublisher.publishEvent(new BidAlertEvent(
                auction.getAuctionId(),
                auction.getCreatedBy(),
                auction.getProductName(),
                currentBid.getBidPrice(),
                AlertType.BID
        ));

        log.info("[auction] HELD 확정 완료 bidId={}, price={}, auctionId={}",
                bidId, currentBid.getBidPrice(), auctionId);
    }

    private void handleFail(UUID bidId) {
        Bid bid = bidRepository.findById(bidId).orElse(null);
        if (bid == null) {
            log.warn("[auction] 실패 이벤트 수신 → 이미 처리되었거나 존재하지 않는 bid. 이벤트 무시. bidId={}", bidId);
            return;
        }

        if (bid.getStatus() != BidStatus.PENDING) {
            log.info("[auction] 중복 실패 이벤트 무시. bidId={}, status={}", bidId, bid.getStatus());
            return;
        }

        bid.markFailed();
        bidRepository.saveAndFlush(bid);

        log.info("[auction] FAILED 반영 완료 bidId={}", bidId);
    }

    private String getTypeId(ConsumerRecord<String, String> record) {
        Header h = record.headers().lastHeader("__TypeId__");
        return (h == null) ? null : new String(h.value(), StandardCharsets.UTF_8);
    }

    private UUID uuid(JsonNode root, String field) {
        if (!root.hasNonNull(field)) return null;
        try {
            return UUID.fromString(root.get(field).asText());
        } catch (Exception e) {
            return null;
        }
    }
}

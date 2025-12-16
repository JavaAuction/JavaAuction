package com.javaauction.auction_service.application.event;

import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.event.AuctionValidationRequestEvent;
import com.javaauction.auction_service.domain.event.AuctionValidationResponseEvent;
import com.javaauction.auction_service.infrastructure.repository.AuctionRepository;
import com.javaauction.auction_service.presentation.advice.AuctionErrorCode;
import com.javaauction.global.presentation.exception.BussinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewEventConsumer {

    private final AuctionKafkaEvent producer;
    private final AuctionRepository auctionRepository;

    @KafkaListener(topics = "auction.validation.request", groupId = "auction-service-group")
    @Transactional
    public void auctionValidRequest(AuctionValidationRequestEvent event, Acknowledgment ack) {

        Auction auction = auctionRepository.findByAuctionIdAndDeletedAtIsNull(event.auctionId())
            .orElseThrow(() -> new BussinessException(AuctionErrorCode.AUCTION_NOT_FOUND)
            );

        try {
            producer.send(
                AuctionValidationResponseEvent.builder()
                    .correlationId(event.correlationId())
                    .sellerId(auction.getUserId())
                    .buyerId(auction.getSuccessfulBidder())
                    .isValid(auction.getSuccessfulBidder().equals(event.userId()))
                    .build()
            );

            ack.acknowledge();

        } catch (KafkaException e) {

            throw new RuntimeException(e);
        }

    }
}

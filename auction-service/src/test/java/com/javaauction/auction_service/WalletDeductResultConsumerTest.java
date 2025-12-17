package com.javaauction.auction_service;

import com.javaauction.auction_service.application.event.WalletDeductResultConsumer;
import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.entity.Bid;
import com.javaauction.auction_service.domain.entity.enums.BidStatus;
import com.javaauction.auction_service.domain.event.BidAlertEvent;
import com.javaauction.auction_service.infrastructure.repository.AuctionRepository;
import com.javaauction.auction_service.infrastructure.repository.BidRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.support.Acknowledgment;
import static org.mockito.ArgumentMatchers.any;


import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WalletDeductResultConsumerTest {

    @InjectMocks
    WalletDeductResultConsumer consumer;

    @Mock
    BidRepository bidRepository;

    @Mock
    AuctionRepository auctionRepository;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    Acknowledgment ack;

    // 결제 성공 이벤트 수신
    @Test
    void 결제성공_이벤트면_입찰상태가_HELD로_변경된다() throws Exception {
        // given
        UUID auctionId = UUID.randomUUID();
        UUID bidId = UUID.randomUUID();

        String json = """
        {
          "auctionId": "%s",
          "bidId": "%s"
        }
        """.formatted(auctionId, bidId);

        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("wallet.deduct.result", 0, 0, null, json);

        record.headers().add(
                "__TypeId__",
                "WalletDeductSucceededEvent".getBytes(StandardCharsets.UTF_8)
        );

        Bid bid = mock(Bid.class);
        given(bid.getStatus()).willReturn(BidStatus.PENDING);
        given(bid.getBidPrice()).willReturn(10_000L);

        Auction auction = mock(Auction.class);
        given(auction.getCurrentPrice()).willReturn(5_000L);


        given(bidRepository.findById(bidId))
                .willReturn(Optional.of(bid));
        given(auctionRepository.findById(auctionId))
                .willReturn(Optional.of(auction));

        // when
        consumer.onMessage(record, ack);

        // then
        verify(bid).markHeld();
        verify(auction).updateCurrentPrice(10_000L);
        verify(eventPublisher).publishEvent(
                any(BidAlertEvent.class)
        );
        verify(ack).acknowledge();
    }

    // 결제 실패 이벤트 수신
    @Test
    void 결제실패_이벤트면_입찰상태가_FAILED로_변경된다() {
        // given
        UUID bidId = UUID.randomUUID();
        UUID auctionId = UUID.randomUUID();

        String json = """
        {
          "auctionId": "%s",
          "bidId": "%s"
        }
        """.formatted(auctionId, bidId);

        ConsumerRecord<String, String> record =
                new ConsumerRecord<>("wallet.deduct.result", 0, 0, null, json);

        record.headers().add(
                "__TypeId__",
                "WalletDeductFailedEvent".getBytes(StandardCharsets.UTF_8)
        );

        Bid bid = mock(Bid.class);
        given(bid.getStatus()).willReturn(BidStatus.PENDING);

        given(bidRepository.findById(bidId))
                .willReturn(Optional.of(bid));

        // when
        consumer.onMessage(record, ack);

        // then
        verify(bid).markFailed();
        verify(ack).acknowledge();
    }

}




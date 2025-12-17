package com.javaauction.auction_service;

import com.javaauction.auction_service.application.event.AuctionKafkaEvent;
import com.javaauction.auction_service.application.service.BidService;
import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.entity.Bid;
import com.javaauction.auction_service.domain.event.BidResult;
import com.javaauction.auction_service.domain.service.BidDomainService;
import com.javaauction.auction_service.infrastructure.client.dto.ReqDeductDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BidServiceTest {

    @InjectMocks
    BidService bidService;

    @Mock
    BidDomainService bidDomainService;

    @Mock
    AuctionKafkaEvent auctionKafkaEvent;

    @Test
    void placeBid_성공시_이벤트를_발행한다() {
        // given
        UUID auctionId = UUID.randomUUID();
        UUID bidId = UUID.randomUUID();

        Bid bid = mock(Bid.class);
        given(bid.getBidId()).willReturn(bidId);

        BidResult result = new BidResult(
                mock(Auction.class),
                null,
                null,
                bid
        );

        given(bidDomainService.placeBidWithLock(
                any(), any(), any(), any()
        )).willReturn(result);

        // when
        bidService.placeBid(auctionId, "user1", "USER", 1000L);

        // then
        verify(auctionKafkaEvent).send(any(ReqDeductDto.class));
    }
}

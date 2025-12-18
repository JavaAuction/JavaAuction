package com.javaauction.auction_service.applivation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.javaauction.auction_service.application.event.AuctionKafkaEvent;
import com.javaauction.auction_service.application.scheduler.AuctionScheduler;
import com.javaauction.auction_service.application.service.impl.AuctionServiceImpl;
import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.entity.Bid;
import com.javaauction.auction_service.domain.entity.enums.AuctionStatus;
import com.javaauction.auction_service.infrastructure.client.ProductFeignClient;
import com.javaauction.auction_service.infrastructure.client.dto.AlertType;
import com.javaauction.auction_service.infrastructure.client.dto.ReqPostInternalAlertsDtoV1;
import com.javaauction.auction_service.infrastructure.client.dto.ReqProductStatusUpdateDto;
import com.javaauction.auction_service.infrastructure.client.dto.ReqProductStatusUpdateDto.ProductStatus;
import com.javaauction.auction_service.infrastructure.client.dto.ReqSettleDto;
import com.javaauction.auction_service.infrastructure.client.dto.TransactionType;
import com.javaauction.auction_service.infrastructure.repository.AuctionRepository;
import com.javaauction.auction_service.infrastructure.repository.BidRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuctionServiceTests {


    @InjectMocks
    AuctionServiceImpl auctionService;

    @InjectMocks
    private AuctionScheduler auctionScheduler;

    @Mock
    AuctionKafkaEvent auctionKafkaEvent;

    @Mock
    AuctionServiceImpl MockService;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private BidRepository bidRepository;

    @Mock
    private ProductFeignClient productFeignClient;

    @Test
    void 유찰() {

        UUID auctionId = UUID.randomUUID();

        Auction auction = mock(Auction.class);
        when(auction.getUserId()).thenReturn("seller");
        when(auction.getProductId()).thenReturn(UUID.randomUUID());
        when(auction.getCurrentPrice()).thenReturn(null);

        when(auctionRepository.findByAuctionIdAndDeletedAtIsNull(auctionId))
            .thenReturn(Optional.of(auction));

        when(bidRepository.findTopByAuctionIdOrderByBidPriceDesc(auctionId))
            .thenReturn(Optional.empty());

        auctionService.auctionEnds(auctionId);

        verify(productFeignClient).updateProductStatus(
            auction.getProductId(),
            ReqProductStatusUpdateDto.builder()
                .finalPrice(null)
                .productStatus(ProductStatus.AUCTION_WAITING)
                .build(),
            auction.getUserId()
        );

        verify(auction).failBid();

        verify(auctionKafkaEvent).send(
            (ReqPostInternalAlertsDtoV1) argThat(req ->
                req instanceof ReqPostInternalAlertsDtoV1 &&
                    ((ReqPostInternalAlertsDtoV1) req).alertType() == AlertType.FAIL
            )
        );

        verify(auctionKafkaEvent, never()).send(
            (ReqSettleDto) argThat(req -> req instanceof ReqSettleDto)
        );
    }

    @Test
    void 낙찰() {

        UUID auctionId = UUID.randomUUID();

        Auction auction = Auction.builder()
            .auctionId(auctionId)
            .productId(UUID.randomUUID())
            .productName("인형")
            .userId("seller")
            .status(AuctionStatus.IN_PROGRESS)
            .build();

        Bid bid = Bid.create(auctionId, "buyer", 3000L);

        when(auctionRepository.findByAuctionIdAndDeletedAtIsNull(auctionId))
            .thenReturn(Optional.of(auction));

        when(bidRepository.findTopByAuctionIdOrderByBidPriceDesc(auctionId))
            .thenReturn(Optional.of(bid));

        auctionService.auctionEnds(auctionId);

        assertEquals(AuctionStatus.SETTLE_RUNNING, auction.getStatus());
        assertEquals("buyer", auction.getSuccessfulBidder());
        assertEquals(3000L, auction.getCurrentPrice());

        verify(productFeignClient).updateProductStatus(
            auction.getProductId(),
            ReqProductStatusUpdateDto.builder()
                .finalPrice(bid.getBidPrice())
                .productStatus(ProductStatus.SOLD)
                .build(),
            bid.getUserId()
        );

        verify(auctionKafkaEvent).send(
            (ReqSettleDto) argThat(req ->
                req instanceof ReqSettleDto settle
                    && settle.getTransactionType() == TransactionType.HOLD
                    && settle.getAmount() == 3000L
                    && settle.getBuyerId().equals(bid.getUserId())
                    && settle.getSellerId().equals(auction.getUserId())
                    && settle.getAuctionId().equals(auctionId)
            )
        );

    }

    @Test
    void 스케줄러_경매종료() {

        Auction auction = Auction.builder()
            .auctionId(UUID.randomUUID())
            .status(AuctionStatus.IN_PROGRESS)
            .endedAt(LocalDateTime.now().minusMinutes(1))
            .build();

        when(auctionRepository.findAllByStatusAndEndedAtBefore(
            eq(AuctionStatus.IN_PROGRESS),
            any(LocalDateTime.class)
        )).thenReturn(List.of(auction));

        // when
        auctionScheduler.endBidScheduler();

        // then
        verify(MockService).auctionEnds(auction.getAuctionId());
    }

}

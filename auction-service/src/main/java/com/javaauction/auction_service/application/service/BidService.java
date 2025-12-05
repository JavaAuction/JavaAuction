package com.javaauction.auction_service.application.service;

import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.entity.Bid;
import com.javaauction.auction_service.domain.event.BidAlertEvent;
import com.javaauction.auction_service.domain.event.BidResult;
import com.javaauction.auction_service.domain.event.OldBidReleaseEvent;
import com.javaauction.auction_service.domain.service.BidDomainService;
import com.javaauction.auction_service.infrastructure.client.AlertClient;
import com.javaauction.auction_service.infrastructure.client.PaymentClient;
import com.javaauction.auction_service.infrastructure.client.dto.*;
import com.javaauction.auction_service.infrastructure.repository.AuctionRepository;
import com.javaauction.auction_service.infrastructure.repository.BidRepository;
import com.javaauction.auction_service.presentation.advice.AuctionErrorCode;
import com.javaauction.auction_service.presentation.advice.BidErrorCode;
import com.javaauction.auction_service.presentation.dto.response.ResGetBidsDto;
import com.javaauction.auction_service.presentation.dto.response.internal.InternalBidDto;
import com.javaauction.auction_service.presentation.dto.response.internal.ResInternalBidsDto;
import com.javaauction.global.presentation.exception.BussinessException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidDomainService bidDomainService;
    private final ApplicationEventPublisher eventPublisher;
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final PaymentClient paymentClient;
    private final AlertClient alertClient;

    /**
     * 입찰 처리 서비스
     */
    @Transactional
    public BidResult placeBid(UUID auctionId, String userId, String role, Long bidPrice) {

        paymentPrecheck(userId, bidPrice);

        BidResult result = bidDomainService.placeBidWithLock(
                auctionId,
                userId,
                role,
                bidPrice
        );

        UUID bidId = result.getNewBid().getBidId();

        // HOLD 요청
        paymentHold(userId, bidPrice, auctionId, bidId);

        // 알림 이벤트
        eventPublisher.publishEvent(new BidAlertEvent(result));

        // 이전 최고 입찰자 상태 RELEASE로 변경하는 이벤트
        eventPublisher.publishEvent(new OldBidReleaseEvent(result));

        return result;
    }

    @Transactional(readOnly = true)
    public ResGetBidsDto getBids(UUID auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new BussinessException(AuctionErrorCode.AUCTION_NOT_FOUND));

        List<Bid> bids = bidRepository.findByAuctionIdOrderByCreatedAtDesc(auctionId);

        List<ResGetBidsDto.BidDto> bidDtos = bids.stream()
                .map(b -> ResGetBidsDto.BidDto.builder()
                        .bidId(b.getBidId())
                        .bidPrice(b.getBidPrice())
                        .createdAt(b.getCreatedAt())
                        .status(b.getStatus())
                        .build()
                )
                .toList();

        return ResGetBidsDto.builder()
                .auctionId(auctionId)
                .productId(auction.getProductId())
                .bids(bidDtos)
                .build();
    }

    @Transactional(readOnly = true)
    public ResInternalBidsDto internalGetBids(String userId) {
        List<InternalBidDto> bids = bidRepository.findInternalBidsByUserId(userId);

        return ResInternalBidsDto.builder()
                .userId(userId)
                .bids(bids)
                .build();
    }

    private void paymentPrecheck(String userId, Long bidPrice) {

        ReqValidateDto req = new ReqValidateDto(
                userId,
                bidPrice
        );

        try {
            paymentClient.validateBalance(req);
        } catch (FeignException.BadRequest e) {
            // 잔액 부족
            throw new BussinessException(BidErrorCode.BID_INSUFFICIENT_BALANCE);
        }
    }

    private void paymentHold(String userId, Long bidPrice, UUID auctionId, UUID bidId) {
        ReqDeductDto req = ReqDeductDto.builder()
                .userId(userId)
                .transactionType(DeductType.HOLD)
                .deductAmount(bidPrice)
                .auctionId(auctionId)
                .bidId(bidId)
                .build();

        try {
            paymentClient.deduct(req);
        } catch (FeignException.BadRequest e) {
            throw new BussinessException(BidErrorCode.BID_INSUFFICIENT_BALANCE);
        }
    }
}

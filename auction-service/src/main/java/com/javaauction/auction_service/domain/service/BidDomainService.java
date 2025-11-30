package com.javaauction.auction_service.domain.service;

import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.entity.Bid;
import com.javaauction.auction_service.domain.event.BidResult;
import com.javaauction.auction_service.domain.entity.enums.AuctionStatus;
import com.javaauction.auction_service.infrastructure.repository.AuctionRepository;
import com.javaauction.auction_service.infrastructure.repository.BidRepository;
import com.javaauction.auction_service.presentation.advice.BidErrorCode;
import com.javaauction.global.presentation.exception.BussinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BidDomainService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;

    /**
     * 비관적 락을 사용해 경매 한 건 단위로 입찰 처리.
     * - 입찰 가능 여부 검증
     * - 성공 시 Auction 상태 변경 + Bid insert
     */
        @Transactional
        public BidResult placeBidWithLock(UUID auctionId, String userId, Long bidPrice) {

            // Auction 비관적 락
            Auction auction = auctionRepository.findByIdForUpdate(auctionId)
                    .orElseThrow(() -> new BussinessException(BidErrorCode.BID_AUCTION_NOT_FOUND));

            if (auction.getStatus() != AuctionStatus.IN_PROGRESS)
                throw new BussinessException(BidErrorCode.BID_FINISHED_AUCTION);

            long currentPrice = auction.getCurrentPrice() != null
                    ? auction.getCurrentPrice()
                    : auction.getStartPrice();

            long minRequired = currentPrice + auction.getUnit();
            if (bidPrice < minRequired)
                throw new BussinessException(BidErrorCode.BID_PRICE_TOO_LOW);

            // 새로운 bid를 저장하기 전에 이전 최고 입찰자 조회
            List<Bid> heldBids = bidRepository.findHeldBidsOrderByPriceDesc(auctionId);
            Bid previousBid = heldBids.isEmpty() ? null : heldBids.get(0);

            String oldUserId = previousBid != null ? previousBid.getUserId() : null;
            Long oldPrice    = previousBid != null ? previousBid.getBidPrice() : null;

            // 새로운 입찰 생성 및 저장
            Bid newBid = Bid.create(auctionId, userId, bidPrice);
            bidRepository.save(newBid);

            // Auction 최고가 + 최고 입찰자 업데이트
            auction.updateCurrentBid(userId, bidPrice);

            return new BidResult(auction, oldUserId, oldPrice, newBid);
        }
    }




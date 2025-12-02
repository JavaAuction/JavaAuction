package com.javaauction.auction_service.infrastructure.repository;

import com.javaauction.auction_service.domain.entity.QAuction;
import com.javaauction.auction_service.domain.entity.QBid;
import com.javaauction.auction_service.presentation.dto.response.internal.InternalBidDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BidQueryDslRepositoryImpl implements BidQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<InternalBidDto> findInternalBidsByUserId(String userId) {

        QBid bid = QBid.bid;
        QAuction auction = QAuction.auction;

        return queryFactory
                .select(Projections.constructor(
                        InternalBidDto.class,
                        bid.bidId,
                        bid.auctionId,
                        auction.productId,
                        bid.bidPrice,
                        bid.status,
                        bid.createdAt
                ))
                .from(bid)
                .join(auction).on(auction.auctionId.eq(bid.auctionId))
                .where(bid.userId.eq(userId))
                .fetch();
    }
}


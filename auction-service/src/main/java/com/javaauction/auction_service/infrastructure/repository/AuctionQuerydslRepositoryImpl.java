package com.javaauction.auction_service.infrastructure.repository;

import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.entity.QAuction;
import com.javaauction.auction_service.domain.entity.enums.AuctionStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuctionQuerydslRepositoryImpl implements AuctionQuerydslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Auction> auctions(Pageable pageable, AuctionStatus status, String keyword) {
        QAuction auction = QAuction.auction;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(auction.deletedAt.isNull());
        if (status != null) {
            builder.and(auction.status.eq(status));
        }
        if (keyword != null) {
            builder.and(auction.productName.contains(keyword));
        }

        List<Auction> auctions = queryFactory.selectFrom(auction)
            .where(builder)
            .orderBy(getSort(pageable, auction))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(auction.count())
            .from(auction)
            .where(builder)
            .fetchOne();

        return new PageImpl<>(auctions, pageable, (total != null) ? total : 0L);
    }

    private <T> OrderSpecifier<?>[] getSort(Pageable pageable, EntityPathBase<T> qClass) {
        return pageable.getSort().stream().map(order ->
                new OrderSpecifier(
                    Order.valueOf(order.getDirection().name()),
                    Expressions.path(Object.class, qClass, order.getProperty())
                )).toList()
            .toArray(new OrderSpecifier[0]);
    }

}

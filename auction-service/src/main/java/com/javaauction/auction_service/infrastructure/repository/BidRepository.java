package com.javaauction.auction_service.infrastructure.repository;

import com.javaauction.auction_service.domain.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BidRepository extends JpaRepository<Bid, UUID> {

    // 이전 최고 입찰자(최고가) 탐색용 쿼리
    @Query("""
        select b from Bid b
        where b.auctionId = :auctionId
          and b.status = 'HELD'
        order by b.bidPrice desc
    """)
    List<Bid> findHeldBidsOrderByPriceDesc(@Param("auctionId") UUID auctionId);

    // RELEASE 처리할 입찰 엔티티를 조회하는 쿼리
    @Query("""
        select b from Bid b
        where b.auctionId = :auctionId
          and b.userId = :userId
          and b.bidPrice = :bidPrice
          and b.status = 'HELD'
    """)
    Optional<Bid> findHeldBidExact(
            @Param("auctionId") UUID auctionId,
            @Param("userId") String userId,
            @Param("bidPrice") Long bidPrice
    );
}



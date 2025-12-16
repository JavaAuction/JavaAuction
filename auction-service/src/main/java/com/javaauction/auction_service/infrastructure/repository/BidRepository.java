package com.javaauction.auction_service.infrastructure.repository;

import com.javaauction.auction_service.domain.entity.Bid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.javaauction.auction_service.domain.entity.enums.BidStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BidRepository extends JpaRepository<Bid, UUID>, BidQueryDslRepository {

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

    // 최신 입찰 순 입찰 내역 조회
    // List<Bid> findByAuctionIdOrderByCreatedAtDesc(UUID auctionId);
    List<Bid> findTop5ByAuctionIdAndStatusInOrderByCreatedAtDesc(
            UUID auctionId,
            List<BidStatus> statuses
    );

    Optional<Bid> findTopByAuctionIdOrderByBidPriceDesc(UUID auctionId);

    @Query("""
        SELECT b
        FROM Bid b
        WHERE b.auctionId = :auctionId
          AND b.bidId <> :currentBidId
          AND b.status IN ('HELD', 'PENDING')
        ORDER BY b.bidPrice DESC
        """)
    Bid findPreviousBid(
            @Param("auctionId") UUID auctionId,
            @Param("currentBidId") UUID currentBidId
    );

    Bid findTopByAuctionIdAndStatusOrderByBidPriceDesc(UUID auctionId, BidStatus bidStatus);
}



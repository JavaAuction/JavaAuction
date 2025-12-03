package com.javaauction.auction_service.infrastructure.repository;

import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.entity.enums.AuctionStatus;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuctionRepository extends JpaRepository<Auction, UUID>, AuctionQuerydslRepository {

    boolean existsByProductIdAndDeletedAtIsNull(UUID productId);

    Optional<Auction> findByAuctionIdAndDeletedAtIsNull(UUID auctionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Auction a where a.auctionId = :auctionId")
    Optional<Auction> findByIdForUpdate(@Param("auctionId") UUID auctionId);

    List<Auction> findAllByStatusAndEndedAtBefore(AuctionStatus auctionStatus, LocalDateTime now);
}

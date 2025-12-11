package com.javaauction.auction_service.infrastructure.repository;

import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.entity.enums.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuctionRepository extends JpaRepository<Auction, UUID>, AuctionQuerydslRepository {

    boolean existsByProductIdAndDeletedAtIsNull(UUID productId);

    Optional<Auction> findByAuctionIdAndDeletedAtIsNull(UUID auctionId);

    List<Auction> findAllByStatusAndEndedAtBefore(AuctionStatus auctionStatus, LocalDateTime now);
}

package com.javaauction.auction_service.infrastructure.repository;

import com.javaauction.auction_service.domain.entity.Auction;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, UUID> {

    boolean existsByProductIdAndDeletedAtIsNull(UUID productId);
}

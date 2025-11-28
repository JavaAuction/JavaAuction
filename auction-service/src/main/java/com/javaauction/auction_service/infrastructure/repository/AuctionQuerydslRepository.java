package com.javaauction.auction_service.infrastructure.repository;

import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.entity.enums.AuctionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuctionQuerydslRepository {

    Page<Auction> auctions(Pageable pageable, AuctionStatus status, String keyword);
}

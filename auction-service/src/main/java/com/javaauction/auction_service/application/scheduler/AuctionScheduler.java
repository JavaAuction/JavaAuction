package com.javaauction.auction_service.application.scheduler;

import com.javaauction.auction_service.application.service.AuctionService;
import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.entity.enums.AuctionStatus;
import com.javaauction.auction_service.infrastructure.repository.AuctionRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionScheduler {

    private final AuctionService auctionService;
    private final AuctionRepository auctionRepository;


    @Scheduled(fixedRate = 60000)
    @Transactional
    public void endBidScheduler() {

        List<Auction> endAuctions = auctionRepository.findAllByStatusAndEndedAtBefore(
            AuctionStatus.IN_PROGRESS, LocalDateTime.now()
        );

        for (Auction auction : endAuctions) {
            auctionService.auctionEnds(auction.getAuctionId());
        }

    }

}

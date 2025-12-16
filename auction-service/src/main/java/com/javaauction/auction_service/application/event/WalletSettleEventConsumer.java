package com.javaauction.auction_service.application.event;

import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.entity.enums.AuctionStatus;
import com.javaauction.auction_service.domain.event.WalletSettleFailedEvent;
import com.javaauction.auction_service.domain.event.WalletSettleSucceededEvent;
import com.javaauction.auction_service.infrastructure.repository.AuctionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletSettleEventConsumer {

    private final AuctionRepository auctionRepository;


    @KafkaListener(topics = "wallet.settle.result", groupId = "auction-service-group")
    @Transactional
    public void settleSuccess(WalletSettleSucceededEvent event) {

        Auction auction = auctionRepository.findById(event.getAuctionId())
            .orElseThrow();

        auction.setStatus(AuctionStatus.SUCCESSFUL_BID);
        auctionRepository.save(auction);
    }

    @KafkaListener(topics = "wallet.settle.result", groupId = "auction-service")
    @Transactional
    public void onFail(WalletSettleFailedEvent event) {

        Auction auction = auctionRepository.findById(event.getAuctionId())
            .orElseThrow();

        auction.setStatus(AuctionStatus.SETTLE_FAIL);
        auctionRepository.save(auction);
    }

}

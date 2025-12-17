package com.javaauction.auction_service.application.event;

import com.javaauction.auction_service.domain.entity.Auction;
import com.javaauction.auction_service.domain.entity.enums.AuctionStatus;
import com.javaauction.auction_service.domain.event.WalletSettleFailedEvent;
import com.javaauction.auction_service.domain.event.WalletSettleSucceededEvent;
import com.javaauction.auction_service.infrastructure.client.dto.AlertType;
import com.javaauction.auction_service.infrastructure.client.dto.ReqPostInternalAlertsDtoV1;
import com.javaauction.auction_service.infrastructure.repository.AuctionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletSettleEventConsumer {

    private final AuctionRepository auctionRepository;
    private final AuctionKafkaEvent auctionKafkaEvent;


    @KafkaListener(topics = "wallet.settle.result", groupId = "auction-service-group")
    @Transactional
    public void settleSuccess(WalletSettleSucceededEvent event, Acknowledgment ack) {

        Auction auction = auctionRepository.findById(event.getAuctionId())
            .orElseThrow();

        auction.setStatus(AuctionStatus.SUCCESSFUL_BID);
        auctionRepository.save(auction);

        ack.acknowledge();
    }

    @KafkaListener(topics = "wallet.settle.result", groupId = "auction-service")
    @Transactional
    public void onFail(WalletSettleFailedEvent event, Acknowledgment ack) {

        Auction auction = auctionRepository.findById(event.getAuctionId())
            .orElseThrow();

        auction.setStatus(AuctionStatus.SETTLE_FAIL);
        auctionRepository.save(auction);

        ReqPostInternalAlertsDtoV1 successReq = ReqPostInternalAlertsDtoV1.builder()
            .auctionId(auction.getAuctionId())
            .alertType(AlertType.SUCCESS)
            .content(String.format("%s의 경매가 %s 님에게 %s 원에 낙찰되었습니다.", auction.getProductName(),
                auction.getSuccessfulBidder(),
                auction.getCurrentPrice()))
            .userId(auction.getUserId())
            .build();

        ReqPostInternalAlertsDtoV1 successBidReq = ReqPostInternalAlertsDtoV1.builder()
            .auctionId(auction.getAuctionId())
            .alertType(AlertType.SUCCESS)
            .content(String.format("%s의 경매가 입찰하신 %s 원에 낙찰되었습니다.", auction.getProductName(),
                auction.getCurrentPrice()))
            .userId(auction.getSuccessfulBidder())
            .build();

        auctionKafkaEvent.send(successReq);
        auctionKafkaEvent.send(successBidReq);

        ack.acknowledge();
    }

}

package com.javaauction.auction_service.application.event;

import com.javaauction.auction_service.domain.event.BidAlertEvent;
import com.javaauction.auction_service.infrastructure.client.dto.ReqPostInternalAlertsDtoV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BidEventHandler {

    private final AuctionKafkaEvent auctionKafkaEvent;

    /**
     * 입찰 확정 성공 시 경매 등록자에게 알림 전송
     */
    @EventListener
    public void handleBidAlert(BidAlertEvent event) {

        ReqPostInternalAlertsDtoV1 req = ReqPostInternalAlertsDtoV1.builder()
                .auctionId(event.getAuctionId())
                .userId(event.getAuctionOwnerId()) // 경매 등록자에게 알림
                .alertType(event.getAlertType())
                .content(String.format(
                        "%s에 새로운 입찰이 발생했습니다. (입찰가 : %d)",
                        event.getProductName(),
                        event.getBidPrice()
                ))
                .build();

        log.info(
                "경매 등록자 '{}'에게 입찰 알림 전송 (auctionId={})",
                event.getAuctionOwnerId(),
                event.getAuctionId()
        );

        auctionKafkaEvent.send(req);
    }
}

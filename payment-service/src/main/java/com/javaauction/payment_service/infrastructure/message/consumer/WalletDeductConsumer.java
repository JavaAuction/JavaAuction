package com.javaauction.payment_service.infrastructure.message.consumer;

import com.javaauction.payment_service.application.service.WalletServiceV1;
import com.javaauction.payment_service.infrastructure.message.event.fail.WalletDeductFailedEvent;
import com.javaauction.payment_service.infrastructure.message.event.request.WalletDeductRequestedEvent;
import com.javaauction.payment_service.infrastructure.message.event.success.WalletDeductSucceededEvent;
import com.javaauction.payment_service.infrastructure.message.producer.WalletEventProducer;
import com.javaauction.payment_service.presentation.advice.PaymentException;
import com.javaauction.payment_service.presentation.dto.request.ReqDeductDto;
import com.javaauction.payment_service.presentation.dto.response.ResDeductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletDeductConsumer {

    private final WalletServiceV1 walletService;
    private final WalletEventProducer walletEventProducer;

    @KafkaListener(
            topics = "auction.wallet.deduct",
            groupId = "payment-service"
    )
    public void onMessage(WalletDeductRequestedEvent event) {
        log.info("[WalletDeductRequestConsumer] auction.wallet.deduct 이벤트 수신: {}", event);

        try {
            // 1. 이벤트 -> DTO 변환
            ReqDeductDto req = ReqDeductDto.builder()
                    .userId(event.getUserId())
                    .transactionType(event.getTransactionType())
                    .deductAmount(event.getDeductAmount())
                    .auctionId(event.getAuctionId())
                    .bidId(event.getBidId())
                    .build();

            // 2. deduct 호출
            ResDeductDto res = walletService.deduct(req);

            // 3. 잔액 차감 성공 시 결과 이벤트 발행
            WalletDeductSucceededEvent successEvent = WalletDeductSucceededEvent.builder()
                    .userId(event.getUserId())
                    .auctionId(event.getAuctionId())
                    .bidId(event.getBidId())
                    .transactionType(event.getTransactionType())
                    .deductAmount(event.getDeductAmount())
                    .walletId(res.getWalletId())
                    .walletTransactionId(res.getWalletTransactionId())
                    .build();

            walletEventProducer.publishWalletDeductSucceeded(successEvent);

        } catch (PaymentException e) {
            // 잔액 차감 실패 시 결과 이벤트 발행
            log.warn("[WalletDeductRequestConsumer] 차감 실패 - 실패 이벤트 발행: {}", e.getResponseCode().getCode());

            WalletDeductFailedEvent failedEvent = WalletDeductFailedEvent.builder()
                    .userId(event.getUserId())
                    .auctionId(event.getAuctionId())
                    .bidId(event.getBidId())
                    .requestedAmount(event.getDeductAmount())
                    .errorCode(e.getResponseCode().getCode())
                    .errorMessage(e.getResponseCode().getMessage())
                    .build();

            walletEventProducer.publishWalletDeductFailed(failedEvent);
        }
    }
}

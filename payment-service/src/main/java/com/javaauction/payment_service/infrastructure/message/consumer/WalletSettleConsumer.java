package com.javaauction.payment_service.infrastructure.message.consumer;

import com.javaauction.payment_service.application.service.WalletTransactionServiceV1;
import com.javaauction.payment_service.infrastructure.message.event.fail.WalletSettleFailedEvent;
import com.javaauction.payment_service.infrastructure.message.event.request.WalletSettleRequestedEvent;
import com.javaauction.payment_service.infrastructure.message.event.success.WalletSettleSucceededEvent;
import com.javaauction.payment_service.infrastructure.message.producer.WalletEventProducer;
import com.javaauction.payment_service.presentation.advice.PaymentException;
import com.javaauction.payment_service.presentation.dto.request.ReqSettleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletSettleConsumer {

    private final WalletTransactionServiceV1 walletTransactionService;
    private final WalletEventProducer walletEventProducer;

    @KafkaListener(
            topics = "wallet.settle.request",
            groupId = "payment-service"
    )
    public void onMessage(WalletSettleRequestedEvent event) {
        log.info("[WalletSettleRequestConsumer] wallet.settle.request 이벤트 수신: {}", event);

        try {
            // 1. 이벤트 -> DTO 변환
            ReqSettleDto req = ReqSettleDto.builder()
                    .buyerId(event.getBuyerId())
                    .sellerId(event.getSellerId())
                    .transactionType(event.getTransactionType())
                    .amount(event.getAmount())
                    .auctionId(event.getAuctionId())
                    .build();

            // 2. settle 호출
            walletTransactionService.settle(req);

            // 3. 정산 성공 시 결과 이벤트 발행
            WalletSettleSucceededEvent successEvent = WalletSettleSucceededEvent.builder()
                    .auctionId(event.getAuctionId())
                    .buyerId(event.getBuyerId())
                    .sellerId(event.getSellerId())
                    .transactionType(event.getTransactionType())
                    .amount(event.getAmount())
                    .build();

            walletEventProducer.publishWalletSettleSucceeded(successEvent);

        } catch (PaymentException e) {
            // 정산 실패 시 결과 이벤트 발행
            log.warn("[WalletSettleRequestConsumer] 정산 실패 - 실패 이벤트 발행: {}", e.getResponseCode().getCode());

            WalletSettleFailedEvent failedEvent = WalletSettleFailedEvent.builder()
                    .auctionId(event.getAuctionId())
                    .buyerId(event.getBuyerId())
                    .sellerId(event.getSellerId())
                    .requestedAmount(event.getAmount())
                    .errorCode(e.getResponseCode().getCode())
                    .errorMessage(e.getResponseCode().getMessage())
                    .build();

            walletEventProducer.publishWalletSettleFailed(failedEvent);
        }
    }
}

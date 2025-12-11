package com.javaauction.payment_service.infrastructure.message.consumer;

import com.javaauction.payment_service.application.service.WalletServiceV1;
import com.javaauction.payment_service.infrastructure.message.event.WalletCreateFailedEvent;
import com.javaauction.payment_service.infrastructure.message.event.WalletCreateRequestedEvent;
import com.javaauction.payment_service.infrastructure.message.event.WalletCreateSucceededEvent;
import com.javaauction.payment_service.infrastructure.message.producer.WalletEventProducer;
import com.javaauction.payment_service.presentation.advice.PaymentException;
import com.javaauction.payment_service.presentation.dto.request.ReqCreateDto;
import com.javaauction.payment_service.presentation.dto.response.ResCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletCreateConsumer {

    private final WalletServiceV1 walletService;
    private final WalletEventProducer walletEventProducer;

    @KafkaListener(
            topics = "wallet.create.request",
            groupId = "payment-service"
    )
    public void onMessage(WalletCreateRequestedEvent event) {
        log.info("[WalletCreateRequestConsumer] wallet.create.request 이벤트 수신: {}", event);

        try {
            // 1. 이벤트 -> DTO 변환
            ReqCreateDto req = ReqCreateDto.builder()
                    .userId(event.getUserId())
                    .build();

            // 2. deduct 호출
            ResCreateDto res = walletService.create(req);

            // 3. 지갑 생성 성공 시 결과 이벤트 발행
            WalletCreateSucceededEvent successEvent = WalletCreateSucceededEvent.builder()
                    .userId(event.getUserId())
                    .walletId(res.getWalletId())
                    .balance(res.getBalance())
                    .build();

            walletEventProducer.publishWalletCreateSucceeded(successEvent);

        } catch (PaymentException e) {
            // 지갑 생성 실패 시 결과 이벤트 발행
            log.warn("[WalletCreateRequestConsumer] 지갑 생성 실패 - 실패 이벤트 발행: {}", e.getResponseCode().getCode());

            WalletCreateFailedEvent failedEvent = WalletCreateFailedEvent.builder()
                    .userId(event.getUserId())
                    .errorCode(e.getResponseCode().getCode())
                    .errorMessage(e.getResponseCode().getMessage())
                    .build();

            walletEventProducer.publishWalletCreateFailed(failedEvent);
        }
    }
}

package com.javaauction.payment_service.infrastructure.message.consumer;

import com.javaauction.payment_service.application.service.WalletServiceV1;
import com.javaauction.payment_service.infrastructure.message.event.fail.WalletDeleteFailedEvent;
import com.javaauction.payment_service.infrastructure.message.event.request.WalletDeleteRequestedEvent;
import com.javaauction.payment_service.infrastructure.message.event.success.WalletDeleteSucceededEvent;
import com.javaauction.payment_service.infrastructure.message.producer.WalletEventProducer;
import com.javaauction.payment_service.presentation.advice.PaymentException;
import com.javaauction.payment_service.presentation.dto.request.ReqDeleteDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletDeleteConsumer {

    private final WalletServiceV1 walletService;
    private final WalletEventProducer walletEventProducer;

    @KafkaListener(
            topics = "wallet.delete",
            groupId = "payment-service"
    )
    public void onMessage(WalletDeleteRequestedEvent event) {
        log.info("[WalletDeleteConsumer] wallet.delete 이벤트 수신: {}", event);

        try {
            // 1. 이벤트 -> DTO 변환
            ReqDeleteDto req = ReqDeleteDto.builder()
                    .userId(event.getUserId())
                    .build();

            // 2. delete 호출
            walletService.delete(req);

            // 3. 지갑 생성 성공 시 결과 이벤트 발행
            WalletDeleteSucceededEvent successEvent = WalletDeleteSucceededEvent.builder()
                    .userId(event.getUserId())
                    .build();

            walletEventProducer.publishWalletDeleteSucceeded(successEvent);

        } catch (PaymentException e) {
            // 지갑 생성 실패 시 결과 이벤트 발행
            log.warn("[WalletDeleteConsumer] 지갑 생성 실패 - 실패 이벤트 발행: {}", e.getResponseCode().getCode());

            WalletDeleteFailedEvent failedEvent = WalletDeleteFailedEvent.builder()
                    .userId(event.getUserId())
                    .errorCode(e.getResponseCode().getCode())
                    .errorMessage(e.getResponseCode().getMessage())
                    .build();

            walletEventProducer.publishWalletDeleteFailed(failedEvent);
        }
    }
}

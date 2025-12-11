package com.javaauction.payment_service.infrastructure.message.producer;

import com.javaauction.payment_service.infrastructure.message.event.fail.WalletCreateFailedEvent;
import com.javaauction.payment_service.infrastructure.message.event.fail.WalletDeductFailedEvent;
import com.javaauction.payment_service.infrastructure.message.event.fail.WalletSettleFailedEvent;
import com.javaauction.payment_service.infrastructure.message.event.success.WalletCreateSucceededEvent;
import com.javaauction.payment_service.infrastructure.message.event.success.WalletDeductSucceededEvent;
import com.javaauction.payment_service.infrastructure.message.event.success.WalletSettleSucceededEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaWalletEventProducer implements WalletEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String CREATE_RESULT_TOPIC = "wallet.create.result";
    private static final String DEDUCT_RESULT_TOPIC = "wallet.deduct.result";
    private static final String SETTLE_RESULT_TOPIC = "wallet.settle.result";

    @Override
    public void publishWalletCreateSucceeded(WalletCreateSucceededEvent event) {
        sendEvent(CREATE_RESULT_TOPIC, event.getUserId(), event);
    }

    @Override
    public void publishWalletCreateFailed(WalletCreateFailedEvent event) {
        sendEvent(CREATE_RESULT_TOPIC, event.getUserId(), event);
    }

    @Override
    public void publishWalletDeductSucceeded(WalletDeductSucceededEvent event) {
        sendEvent(DEDUCT_RESULT_TOPIC, event.getAuctionId().toString(), event);
    }

    @Override
    public void publishWalletDeductFailed(WalletDeductFailedEvent event) {
        sendEvent(DEDUCT_RESULT_TOPIC, event.getAuctionId().toString(), event);
    }

    @Override
    public void publishWalletSettleSucceeded(WalletSettleSucceededEvent event) {
        sendEvent(SETTLE_RESULT_TOPIC, event.getAuctionId().toString(), event);
    }

    @Override
    public void publishWalletSettleFailed(WalletSettleFailedEvent event) {
        sendEvent(SETTLE_RESULT_TOPIC, event.getBuyerId(), event);
    }

    private void sendEvent(String topic, String key, Object event) {
        try {
            log.info("[KafkaWalletEventProducer] 결과 이벤트 발행: topic={}, key={}, payload={}", topic, key, event);

            kafkaTemplate.send(topic, key, event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("[KafkaWalletEventProducer] 이벤트 발행 중 예외 발생: {}", ex.getMessage(), ex);
                        } else {
                            log.info(
                                    "[KafkaWalletEventProducer] 이벤트 발행 성공: partition={}, offset={}",
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset()
                            );
                        }
                    });

        } catch (Exception e) {
            log.error("[KafkaWalletEventProducer] 결과 이벤트 발행 실패: {}", e.getMessage(), e);
        }
    }
}

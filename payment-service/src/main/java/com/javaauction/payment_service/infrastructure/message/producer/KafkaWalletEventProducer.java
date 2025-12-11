package com.javaauction.payment_service.infrastructure.message.producer;

import com.javaauction.payment_service.infrastructure.message.event.WalletDeductFailedEvent;
import com.javaauction.payment_service.infrastructure.message.event.WalletDeductSucceededEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaWalletEventProducer implements WalletEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String DEDUCT_RESULT_TOPIC = "wallet.deduct.result";

    @Override
    public void publishWalletDeductSucceeded(WalletDeductSucceededEvent event) {
        sendEvent(DEDUCT_RESULT_TOPIC, event.getUserId(), event);
    }

    @Override
    public void publishWalletDeductFailed(WalletDeductFailedEvent event) {
        sendEvent(DEDUCT_RESULT_TOPIC, event.getUserId(), event);
    }

    private void sendEvent(String topic, String key, Object event) {
        try {
            log.info("[KafkaWalletEventProducer] deduct 결과 이벤트 발행: topic={}, key={}, payload={}", topic, key, event);

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
            log.error("[KafkaWalletEventProducer] deduct 결과 이벤트 발행 실패: {}", e.getMessage(), e);
        }
    }
}

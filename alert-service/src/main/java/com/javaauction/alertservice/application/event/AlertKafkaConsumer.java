package com.javaauction.alertservice.application.event;

import com.javaauction.alertservice.application.service.AlertServiceV1;
import com.javaauction.alertservice.presentation.dto.request.ReqPostInternalAlertsDtoV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertKafkaConsumer {

    private final AlertServiceV1 alertService;

    @KafkaListener(topics = "auction-alert-topic", groupId = "alert-group",
                   containerFactory = "kafkaListenerContainerFactory")
    public void handleAlertMessage(ReqPostInternalAlertsDtoV1 message) {
        log.info("카프카 메시지 수신: {}", message);

        try {
            alertService.postInternalAlerts(message);
            log.info("알림 저장 성공: {}", message.getUserId());

        } catch (Exception e) {
            log.error("알림 저장 실패: {}", message, e);
        }
    }
}
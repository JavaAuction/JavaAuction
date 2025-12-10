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

    @KafkaListener(topics = "auction-alert-topic", groupId = "alert-service-group") // 임시 topics 설정
    public void handleAlertMessage(ReqPostInternalAlertsDtoV1 message) {

        // 알림 생성
        alertService.postInternalAlerts(message);
    }
}
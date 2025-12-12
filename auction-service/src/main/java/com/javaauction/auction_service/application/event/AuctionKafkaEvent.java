package com.javaauction.auction_service.application.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaauction.auction_service.infrastructure.client.dto.ReqPostInternalAlertsDtoV1;
import com.javaauction.auction_service.infrastructure.client.dto.ReqSettleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionKafkaEvent {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(ReqPostInternalAlertsDtoV1 req) {
        kafkaTemplate.send("auction-alert-topic", req);
    }

    public void send(ReqSettleDto req) {
        kafkaTemplate.send("auction-payment-topic", req);
    }


}

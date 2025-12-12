package com.javaauction.auction_service.application.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventListener {

    //    @KafkaListener(topics = "payment-topic", groupId = "payment-group")
    public void listen(String message) {

    }
}

package com.javaauction.alertservice.application.event;

import com.javaauction.alertservice.domain.entity.Alert;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AlertCreatedEvent {
    private final Alert alert;
}
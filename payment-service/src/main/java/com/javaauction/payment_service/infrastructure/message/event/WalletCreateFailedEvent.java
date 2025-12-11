package com.javaauction.payment_service.infrastructure.message.event;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class WalletCreateFailedEvent {

    private String userId;

    private String errorCode;
    private String errorMessage;
}

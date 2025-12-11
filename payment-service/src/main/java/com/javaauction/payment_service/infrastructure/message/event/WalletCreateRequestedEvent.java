package com.javaauction.payment_service.infrastructure.message.event;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class WalletCreateRequestedEvent {

    private String userId;
}

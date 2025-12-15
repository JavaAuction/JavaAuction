package com.javaauction.payment_service.infrastructure.message.event.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class WalletDeleteRequestedEvent {

    private String userId;
}

package com.javaauction.payment_service.infrastructure.message.event.success;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class WalletDeleteSucceededEvent {

    private String userId;
}

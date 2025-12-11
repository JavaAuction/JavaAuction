package com.javaauction.payment_service.infrastructure.message.event;

import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class WalletCreateSucceededEvent {

    private String userId;

    private UUID walletId;
    private Long balance;
}

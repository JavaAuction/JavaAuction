package com.javaauction.user.infrastructure.external.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletCreateEvent {
    private String userId;
}


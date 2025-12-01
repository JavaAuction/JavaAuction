package com.javaauction.payment_service.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.With;

import java.util.UUID;

@Getter
@Builder
@With
@EqualsAndHashCode(of = "id")
public class Wallet {

    private final UUID id;
    private final String userId;

    @Builder.Default
    private final Long balance = 0L;
}

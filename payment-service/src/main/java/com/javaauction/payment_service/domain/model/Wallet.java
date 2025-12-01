package com.javaauction.payment_service.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@EqualsAndHashCode(of = "id")
public class Wallet {

    private final UUID id;
    private final String userId;
    private final Type type;
    private final Long balance;

    public enum Type {
        CHARGE,
        WITHDRAW
    }
}

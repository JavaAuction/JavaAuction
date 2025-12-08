package com.javaauction.user.infrastructure.external.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResCreateWalletDto {

    private UUID walletId;
    private String userId;
    private Long balance;
}

package com.javaauction.user.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReqCreateAddressDto {
    private String address;
    private String postcode;
    private String addressDetail;
    private boolean isDefault;
}


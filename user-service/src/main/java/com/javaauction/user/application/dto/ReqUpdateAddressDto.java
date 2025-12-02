package com.javaauction.user.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReqUpdateAddressDto {
    private String address;
    private String postcode;
    private String addressDetail;
    private boolean isDefault;
}

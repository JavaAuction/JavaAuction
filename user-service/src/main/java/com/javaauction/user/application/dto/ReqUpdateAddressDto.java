package com.javaauction.user.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("default")
    private boolean isDefault;
}

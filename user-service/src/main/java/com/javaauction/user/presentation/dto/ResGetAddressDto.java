package com.javaauction.user.presentation.dto;

import com.javaauction.user.domain.entity.AddressEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResGetAddressDto {
    private String username;
    private String address;
    private String postcode;
    private String addressDetail;
    private boolean isDefault;

    public static ResGetAddressDto of(AddressEntity address){
        return ResGetAddressDto.builder()
                .username(address.getUser().getUsername())
                .address(address.getAddress())
                .postcode(address.getPostcode())
                .addressDetail(address.getDetail())
                .isDefault(address.isDefault())
                .build();
    }
}

package com.javaauction.user.domain.entity;

import com.javaauction.global.infrastructure.entity.BaseEntity;
import com.javaauction.user.application.dto.ReqUpdateAddressDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_address")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "address_id", nullable = false, unique = true)
    private UUID addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String postcode;

    @Column(nullable = false)
    private String detail;

    @Column(nullable = false)
    private boolean isDefault;

    public void changeDefaultAddress(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public void update(ReqUpdateAddressDto addressDto) {
        if(addressDto.getAddress() != null && !addressDto.getAddress().isEmpty()) {
            this.address = addressDto.getAddress();
        }
        if(addressDto.getPostcode() != null && !addressDto.getPostcode().isEmpty()) {
            this.postcode = addressDto.getPostcode();
        }
        if(addressDto.getAddressDetail() != null && !addressDto.getAddressDetail().isEmpty()) {
            this.detail = addressDto.getAddressDetail();
        }
        if(addressDto.isDefault() != this.isDefault) {
            this.isDefault = addressDto.isDefault();
        }
    }
}

package com.javaauction.user.presentation.dto;

import com.javaauction.user.domain.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResGetAllDto {
    private String username;

    private String email;

    private String name;

    private String slackId;

    private String role;

    private String address;

    private Double rating;

    private Instant createdAt;

    public static ResGetAllDto of(UserEntity user, String addressString, double rating){
        return ResGetAllDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .slackId(user.getSlackId())
                .role(user.getRole().name())
                .address(addressString)
                .rating(rating)
                .createdAt(user.getCreatedAt())
                .build();
    }
}

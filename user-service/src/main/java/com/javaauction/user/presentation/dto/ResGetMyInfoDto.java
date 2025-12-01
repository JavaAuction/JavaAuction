package com.javaauction.user.presentation.dto;

import com.javaauction.user.domain.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResGetMyInfoDto {
    private String username;

    private String email;

    private String name;

    private String slackId;

    private UUID address;

    public static ResGetMyInfoDto of(UserEntity user) {
        return ResGetMyInfoDto.builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .slackId(user.getSlackId())
                .address(user.getAddress())
                .build();
    }
}

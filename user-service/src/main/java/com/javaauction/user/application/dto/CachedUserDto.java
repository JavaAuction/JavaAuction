package com.javaauction.user.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CachedUserDto {

    private String username;
    private String email;
    private String name;
    private String slackId;
    private String role;
    private String address;
    private Instant createdAt;
}

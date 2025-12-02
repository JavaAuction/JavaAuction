package com.example.review.infrastructure.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResGetUserIntDto {
    private String username;
    private String email;
    private String address;
    private String slackId;
    private String role;
}


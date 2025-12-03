package com.javaauction.user.presentation.dto;

import com.javaauction.user.domain.entity.UserEntity;
import com.javaauction.user.infrastructure.external.dto.GetReviewIntDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResGetUserAdminDto {
    private String username;

    private String email;

    private String name;

    private String password;

    private String slackId;

    private String role;

    private UUID address;

    private Double rating;

    private Instant createdAt;

    private List<GetReviewIntDto> reviews;



    public static ResGetUserAdminDto of(UserEntity user) {
        return ResGetUserAdminDto.builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .slackId(user.getSlackId())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static ResGetUserAdminDto of(UserEntity user, Double rating, List<GetReviewIntDto> reviews) {
        return ResGetUserAdminDto.builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .slackId(user.getSlackId())
                .createdAt(user.getCreatedAt())
                .rating(rating)
                .reviews(reviews)
                .build();
    }
}

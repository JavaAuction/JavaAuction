package com.javaauction.user.presentation.dto;

import com.javaauction.user.domain.entity.UserEntity;
import com.javaauction.user.infrastructure.external.dto.GetReviewIntDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
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

    private String address;

    private double rating;

    private List<GetReviewIntDto> reviews;

    public static ResGetMyInfoDto of(UserEntity user) {
        return ResGetMyInfoDto.builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .slackId(user.getSlackId())
                .build();
    }

    public static ResGetMyInfoDto of(UserEntity user, String address, Double rating, List<GetReviewIntDto> reviews) {
        return ResGetMyInfoDto.builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .slackId(user.getSlackId())
                .address(address)
                .rating(rating)
                .reviews(reviews)
                .build();
    }
}

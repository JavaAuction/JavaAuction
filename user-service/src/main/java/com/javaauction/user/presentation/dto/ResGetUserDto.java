package com.javaauction.user.presentation.dto;

import com.javaauction.user.domain.entity.UserEntity;
import com.javaauction.user.infrastructure.external.dto.GetReviewIntDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResGetUserDto {
    private String username;

    private Double rating;

    private List<GetReviewIntDto> reviews;

    public static ResGetUserDto of(UserEntity user){
        return ResGetUserDto.builder()
                .username(user.getUsername())
                .build();
    }

    public static ResGetUserDto of(UserEntity user, Double rating, List<GetReviewIntDto> reviews){
        return ResGetUserDto.builder()
                .username(user.getUsername())
                .rating(rating)
                .reviews(reviews)
                .build();
    }
}

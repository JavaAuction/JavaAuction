package com.javaauction.user.presentation.dto;

import com.javaauction.user.domain.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResGetUserDto {
    private String username;

    public static ResGetUserDto of(UserEntity user){
        return ResGetUserDto.builder()
                .username(user.getUsername())
                .build();
    }
}

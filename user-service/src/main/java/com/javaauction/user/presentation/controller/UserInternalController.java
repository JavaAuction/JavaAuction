package com.javaauction.user.presentation.controller;

import com.javaauction.user.application.service.UserServiceV1;
import com.javaauction.user.presentation.dto.ResGetUserIntDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
@Tag(name = "내부 통신용 사용자 API", description = "내부 서비스용 사용자 관련 API입니다.")
public class UserInternalController {

    private final UserServiceV1 userService;

    @Operation(summary = "사용자 정보 조회",description = "사용자의 정보를 조회합니다.")
    @GetMapping("/{userId}")
    public ResGetUserIntDto getUser(@PathVariable String userId) {
        return userService.getUserInternal(userId);
    }

    @Operation(summary = "사용자 존재 유무 확인",description = "사용자가 존재하는지를 확인합니다.")
    @GetMapping("/{userId}/exists")
    public boolean existsUser(@PathVariable String userId) {
        return userService.existsUser(userId);
    }
}

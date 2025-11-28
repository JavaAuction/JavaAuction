package com.javaauction.user.presentation.controller;

import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.user.application.dto.ReqLoginDto;
import com.javaauction.user.application.dto.ReqSignupDto;
import com.javaauction.user.application.service.UserServiceV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class UserControllerV1 {
    private final UserServiceV1 userService;

    @PostMapping("/auth/signup")
    public ApiResponse signup(@RequestBody ReqSignupDto signupRequestDto) {
        return userService.signup(signupRequestDto);
    }

    @PostMapping("/auth/login")
    public ApiResponse login(@RequestBody ReqLoginDto loginRequestDto) {
        return userService.login(loginRequestDto);
    }
}

package com.javaauction.user.presentation.controller;

import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.user.application.dto.ReqLoginDto;
import com.javaauction.user.application.dto.ReqSignupDto;
import com.javaauction.user.application.service.UserServiceV1;
import com.javaauction.user.infrastructure.JWT.JwtUserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/users")
    public ApiResponse getAllUsers(@RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                   @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
                                   @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {
        return userService.getAllUsers(page - 1, size, sortBy, isAsc, JwtUserContext.getRoleFromHeader());
    }

    @GetMapping("/users/{userId}")
    public ApiResponse getUser(@PathVariable String userId) {
        return userService.getUser(userId, JwtUserContext.getRoleFromHeader());
    }

    @GetMapping("/users/me")
    public ApiResponse getMyInfo(){
        return userService.getMyInfo(JwtUserContext.getUsernameFromHeader());
    }
}

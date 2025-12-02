package com.javaauction.user.presentation.controller;

import com.javaauction.user.application.service.UserServiceV1;
import com.javaauction.user.presentation.dto.ResGetUserIntDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class UserInternalController {

    private final UserServiceV1 userService;

    @GetMapping("/{userId}")
    public ResGetUserIntDto getUser(@PathVariable String userId) {
        return userService.getUserInternal(userId);
    }

    @GetMapping("/{userId}/exists")
    public boolean existsUser(@PathVariable String userId) {
        return userService.existsUser(userId);
    }
}

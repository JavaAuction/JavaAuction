package com.javaauction.user.presentation.controller;

import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.user.application.dto.ReqLoginDto;
import com.javaauction.user.application.dto.ReqSignupDto;
import com.javaauction.user.application.dto.ReqUpdateDto;
import com.javaauction.user.application.service.UserServiceV1;
import com.javaauction.user.infrastructure.JWT.JwtUserContext;
import com.javaauction.user.presentation.advice.UserSuccessCode;
import com.javaauction.user.presentation.dto.ResGetMyInfoDto;
import com.javaauction.user.presentation.dto.ResGetUserAdminDto;
import com.javaauction.user.presentation.dto.ResLoginDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class UserControllerV1 {

    private final UserServiceV1 userService;

    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody ReqSignupDto req) {
        userService.signup(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(UserSuccessCode.USER_CREATED));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<ResLoginDto>> login(@RequestBody ReqLoginDto req) {
        ResLoginDto response = userService.login(req);
        return ResponseEntity
                .ok(ApiResponse.success(UserSuccessCode.LOGIN_SUCCESS, response));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<ResGetUserAdminDto>>> getAllUsers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {
        Page<ResGetUserAdminDto> result = userService.getAllUsers(page - 1, size, sortBy, isAsc, JwtUserContext.getRoleFromHeader());

        return ResponseEntity.ok(ApiResponse.success(UserSuccessCode.USER_LIST_FOUND, result));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Object>> getUser(@PathVariable String userId) {
        Object dto = userService.getUser(userId, JwtUserContext.getUsernameFromHeader(), JwtUserContext.getRoleFromHeader());

        return ResponseEntity.ok(ApiResponse.success(UserSuccessCode.USER_FOUND, dto));
    }

    @GetMapping("/users/me")
    public ResponseEntity<ApiResponse<ResGetMyInfoDto>> getMyInfo() {
        ResGetMyInfoDto dto = userService.getMyInfo(JwtUserContext.getUsernameFromHeader());

        return ResponseEntity.ok(ApiResponse.success(UserSuccessCode.MY_INFO_FOUND, dto));
    }

    @PutMapping("/users/me")
    public ResponseEntity<ApiResponse<Void>> updateUserInfo(@RequestBody ReqUpdateDto req) {
        userService.updateUser(req, JwtUserContext.getUsernameFromHeader());

        return ResponseEntity.ok(ApiResponse.success(UserSuccessCode.USER_UPDATED));
    }

    @DeleteMapping("/users/{userId}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId, JwtUserContext.getUsernameFromHeader(), JwtUserContext.getRoleFromHeader());

        return ResponseEntity.ok(ApiResponse.success(UserSuccessCode.USER_DELETED));
    }
}


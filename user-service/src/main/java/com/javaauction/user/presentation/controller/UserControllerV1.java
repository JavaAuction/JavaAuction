package com.javaauction.user.presentation.controller;

import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.user.application.dto.ReqLoginDto;
import com.javaauction.user.application.dto.ReqSignupDto;
import com.javaauction.user.application.dto.ReqUpdateDto;
import com.javaauction.user.application.service.UserServiceV1;
import com.javaauction.user.infrastructure.JWT.JwtUserContext;
import com.javaauction.user.infrastructure.external.dto.ResInternalBidsDto;
import com.javaauction.user.presentation.advice.UserSuccessCode;
import com.javaauction.user.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "사용자 API", description = "사용자 관련 기능 API입니다.")
public class UserControllerV1 {

    private final UserServiceV1 userService;

    @Operation(summary = "사용자 등록", description = "사용자 가입 정보로 사용자를 등록")
    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody ReqSignupDto req) {
        userService.signup(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(UserSuccessCode.USER_CREATED));
    }

    @Operation(summary = "사용자 로그인", description = "username과 password로 로그인")
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<ResLoginDto>> login(@RequestBody ReqLoginDto req) {
        ResLoginDto response = userService.login(req);
        return ResponseEntity
                .ok(ApiResponse.success(UserSuccessCode.LOGIN_SUCCESS, response));
    }

    @Operation(summary = "사용자 목록 조회", description = "전체 사용자를 조회(관리자 전용)")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<ResGetAllDto>>> getAllUsers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {
        Page<ResGetAllDto> result = userService.getAllUsers(page - 1, size, sortBy, isAsc, JwtUserContext.getRoleFromHeader());

        return ResponseEntity.ok(ApiResponse.success(UserSuccessCode.USER_LIST_FOUND, result));
    }

    @Operation(summary = "사용자 상세조회", description = "사용자 상세 정보를 조회")
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Object>> getUser(@PathVariable String userId) {
        Object dto = userService.getUser(userId, JwtUserContext.getUsernameFromHeader(), JwtUserContext.getRoleFromHeader());

        return ResponseEntity.ok(ApiResponse.success(UserSuccessCode.USER_FOUND, dto));
    }

    @Operation(summary = "본인 정보 조회", description = "본인의 정보를 조회")
    @GetMapping("/users/me")
    public ResponseEntity<ApiResponse<ResGetMyInfoDto>> getMyInfo() {
        ResGetMyInfoDto dto = userService.getMyInfo(JwtUserContext.getUsernameFromHeader());

        return ResponseEntity.ok(ApiResponse.success(UserSuccessCode.MY_INFO_FOUND, dto));
    }

    @Operation(summary = "본인 정보 수정", description = "본인의 정보를 수정")
    @PutMapping("/users/me")
    public ResponseEntity<ApiResponse<Void>> updateUserInfo(@RequestBody ReqUpdateDto req) {
        userService.updateUser(req, JwtUserContext.getUsernameFromHeader());

        return ResponseEntity.ok(ApiResponse.success(UserSuccessCode.USER_UPDATED));
    }

    @Operation(summary = "사용자 삭제", description = "사용자와 관련된 정보를 삭제")
    @DeleteMapping("/users/{userId}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId, JwtUserContext.getUsernameFromHeader(), JwtUserContext.getRoleFromHeader());

        return ResponseEntity.ok(ApiResponse.success(UserSuccessCode.USER_DELETED));
    }

    @Operation(summary = "본인 입찰 조회", description = "본인의 입찰 정보를 조회")
    @GetMapping("/users/me/bids")
    public ResponseEntity<ApiResponse<ResInternalBidsDto>> getMyBids(){
        return userService.getUserBids(JwtUserContext.getUsernameFromHeader(), JwtUserContext.getRoleFromHeader());
    }

}


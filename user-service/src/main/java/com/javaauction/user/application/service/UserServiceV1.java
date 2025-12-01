package com.javaauction.user.application.service;

import com.javaauction.global.infrastructure.code.BaseErrorCode;
import com.javaauction.global.infrastructure.code.BaseSuccessCode;
import com.javaauction.global.presentation.exception.BussinessException;
import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.user.application.dto.ReqLoginDto;
import com.javaauction.user.application.dto.ReqSignupDto;
import com.javaauction.user.application.dto.ReqUpdateDto;
import com.javaauction.user.domain.entity.UserEntity;
import com.javaauction.user.domain.repository.UserRepository;
import com.javaauction.user.infrastructure.JWT.JwtUtil;
import com.javaauction.user.presentation.advice.UserErrorCode;
import com.javaauction.user.presentation.dto.ResGetMyInfoDto;
import com.javaauction.user.presentation.dto.ResGetUserAdminDto;
import com.javaauction.user.presentation.dto.ResGetUserDto;
import com.javaauction.user.presentation.dto.ResLoginDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceV1 {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public void signup(ReqSignupDto signupRequestDto) {
        //이미 존재하는 정보인지 확인
        if (userRepository.findByUsername(signupRequestDto.getUsername()).isPresent()) {
            throw new BussinessException(UserErrorCode.USER_ALREADY_EXISTS);
        }
        if (userRepository.findByEmail(signupRequestDto.getEmail()).isPresent()) {
            throw new BussinessException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.findBySlackId(signupRequestDto.getSlackId()).isPresent()) {
            throw new BussinessException(UserErrorCode.SLACK_ID_ALREADY_EXISTS);
        }

        UserEntity user =  UserEntity.builder()
                .username(signupRequestDto.getUsername())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .email(signupRequestDto.getEmail())
                .name(signupRequestDto.getName())
                .slackId(signupRequestDto.getSlackId())
                .role(signupRequestDto.getRole())
                .build();

        user.setCreate(Instant.now(),"System");
        userRepository.save(user);
    }

    public ResLoginDto login(ReqLoginDto loginRequestDto) {
            // 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getUsername(),
                            loginRequestDto.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 사용자 정보 조회
            UserEntity user = userRepository.findByUsername(loginRequestDto.getUsername())
                    .orElseThrow(() -> new BussinessException(UserErrorCode.USER_NOT_FOUND));

            // JWT 토큰 생성
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

            // 응답 DTO 생성
            return ResLoginDto.builder()
                    .token(token)
                    .username(user.getUsername())
                    .role(user.getRole().name())
                    .build();
    }

    public Page<ResGetUserAdminDto> getAllUsers(int page, int size, String sortBy, boolean isAsc, String role) {
        // 권한 체크를 먼저 수행 (쿼리 실행 전)
        if(role == null || !role.equals("ADMIN")){
            throw new BussinessException(BaseErrorCode.ACCESS_DENIED);
        }

        if(size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        //sortBy가 null이거나, 비어있거나, 공백이거나, "modifiedAt"이 아닌 경우 "createdAt"으로 설정
        if(sortBy == null || sortBy.isEmpty() || sortBy.isBlank() || !sortBy.equals("modifiedAt")) {
            sortBy = "createdAt";
        }

        Pageable pageable = PageRequest.of(
                page > 0 ? page - 1 : page,
                size,
                isAsc ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy
        );

        Page<UserEntity> userList = userRepository.getUsers(pageable);
        log.info("유저 목록 조회 완료 - 총 {}건, 현재 페이지: {}", userList.getTotalElements(), page);

        return userList.map(ResGetUserAdminDto::of);
    }

    public Object getUser(String userId, String username, String role) {
        // 권한 체크는 하지 않지만, role이 null인 경우 기본값 처리
        UserEntity user = userRepository.findByUsername(userId).orElseThrow(() -> new BussinessException(UserErrorCode.USER_NOT_FOUND));

        //삭제된 사용자일 경우
        if(user.getDeletedAt() != null){
            throw new BussinessException(UserErrorCode.CANNOT_DELETE_DELETED_USER);
        }

        // 자기 자신을 검색할 경우
        if(user.getUsername().equals(username)){
            return ApiResponse.success(BaseSuccessCode.OK, ResGetMyInfoDto.of(user));
        }

        //권한에 따라 보이는 정보가 다름
        if(role != null && role.equals("ADMIN")){
            return ApiResponse.success(BaseSuccessCode.OK, ResGetUserAdminDto.of(user));
        }else{
            return ApiResponse.success(BaseSuccessCode.OK, ResGetUserDto.of(user));
        }
    }

    public ResGetMyInfoDto getMyInfo(String username) {
        UserEntity my = userRepository.findByUsername(username).orElseThrow(() -> new BussinessException(UserErrorCode.USER_NOT_FOUND));

        return ResGetMyInfoDto.of(my);
    }

    @Transactional
    public void updateUser(ReqUpdateDto updateRequestDto, String username) {
        UserEntity me = userRepository.findByUsername(username).orElseThrow(() -> new BussinessException(UserErrorCode.USER_NOT_FOUND));

        me.update(ReqUpdateDto.builder().name(updateRequestDto.getName())
                .email(updateRequestDto.getEmail())
                .build());

        me.setUpdated(Instant.now(), username);
    }

    @Transactional
    public void deleteUser(String userId, String username, String role) {
        //본인 또는 관리자가 아닐 경우
        if(!userId.equals(username) && !role.equals("ADMIN")){
            throw new BussinessException(BaseErrorCode.ACCESS_DENIED);
        }

        UserEntity user = userRepository.findByUsername(userId).orElseThrow(() -> new BussinessException(UserErrorCode.USER_NOT_FOUND));

        if(user.getDeletedAt() != null){
            throw new BussinessException(UserErrorCode.CANNOT_DELETE_DELETED_USER);
        }

        user.softDelete(Instant.now(), username);
    }
}

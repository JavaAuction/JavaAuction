package com.javaauction.user.application.service;

import com.javaauction.global.infrastructure.code.BaseErrorCode;
import com.javaauction.global.infrastructure.code.BaseSuccessCode;
import com.javaauction.global.presentation.exception.BussinessException;
import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.user.application.dto.ReqLoginDto;
import com.javaauction.user.application.dto.ReqSignupDto;
import com.javaauction.user.application.dto.ResLoginDto;
import com.javaauction.user.domain.repository.UserRepository;
import com.javaauction.user.domain.entity.UserEntity;
import com.javaauction.user.infrastructure.JWT.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public ApiResponse signup(ReqSignupDto signupRequestDto) {
        if(userRepository.findByUsername(signupRequestDto.getUsername()).isPresent()
                ||userRepository.findByEmail(signupRequestDto.getEmail()).isPresent()
                ||userRepository.findBySlackId(signupRequestDto.getSlackId()).isPresent()){
            log.warn("회원가입 실패 - 중복된 정보");
            throw new BussinessException(BaseErrorCode.INVALID_INPUT_VALUE);
        }

        UserEntity user =  UserEntity.builder()
                .username(signupRequestDto.getUsername())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .email(signupRequestDto.getEmail())
                .slackId(signupRequestDto.getSlackId())
                .role(signupRequestDto.getRole())
                .build();
        user.setCreate(Instant.now(),"System");
        userRepository.save(user);

        log.info("회원가입 성공 - username: {}, role: {}", user.getUsername(), user.getRole());

        return ApiResponse.success(BaseSuccessCode.OK);
    }

    public ApiResponse login(ReqLoginDto loginRequestDto) {
        try {
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
                    .orElseThrow(() -> new BussinessException(BaseErrorCode.INVALID_INPUT_VALUE));

            // JWT 토큰 생성
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

            // 응답 DTO 생성
            ResLoginDto loginResponse = ResLoginDto.builder()
                    .token(token)
                    .username(user.getUsername())
                    .role(user.getRole().name())
                    .build();

            log.info("로그인 성공 - username: {}", user.getUsername());

            return ApiResponse.success(BaseSuccessCode.OK, loginResponse);
        } catch (Exception e) {
            log.warn("로그인 실패 - username: {}, error: {}", loginRequestDto.getUsername(), e.getMessage());
            throw new BussinessException(BaseErrorCode.INVALID_INPUT_VALUE);
        }
    }
}

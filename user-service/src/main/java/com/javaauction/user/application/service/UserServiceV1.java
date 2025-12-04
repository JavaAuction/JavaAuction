package com.javaauction.user.application.service;

import com.javaauction.global.infrastructure.code.BaseErrorCode;
import com.javaauction.global.infrastructure.code.BaseSuccessCode;
import com.javaauction.global.presentation.exception.BussinessException;
import com.javaauction.global.presentation.response.ApiResponse;
import com.javaauction.user.application.dto.ReqLoginDto;
import com.javaauction.user.application.dto.ReqSignupDto;
import com.javaauction.user.application.dto.ReqUpdateDto;
import com.javaauction.user.domain.entity.AddressEntity;
import com.javaauction.user.domain.entity.UserEntity;
import com.javaauction.user.domain.repository.AddressRepository;
import com.javaauction.user.domain.repository.UserRepository;
import com.javaauction.user.infrastructure.JWT.JwtUtil;
import com.javaauction.user.infrastructure.external.client.ReviewServiceClient;
import com.javaauction.user.infrastructure.external.dto.GetReviewIntDto;
import com.javaauction.user.presentation.advice.UserErrorCode;
import com.javaauction.user.presentation.dto.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceV1 {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final AddressRepository addressRepository;
    private final ReviewServiceClient reviewServiceClient;

    public void signup(ReqSignupDto dto) {

        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new BussinessException(UserErrorCode.USER_ALREADY_EXISTS);
        }
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BussinessException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.findBySlackId(dto.getSlackId()).isPresent()) {
            throw new BussinessException(UserErrorCode.SLACK_ID_ALREADY_EXISTS);
        }

        UserEntity user = UserEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .name(dto.getName())
                .slackId(dto.getSlackId())
                .role(dto.getRole())
                .build();

        user.setCreate(Instant.now(), "System");
        userRepository.save(user);
    }


    public ResLoginDto login(ReqLoginDto dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), null)
        );

        UserEntity user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new BussinessException(UserErrorCode.USER_NOT_FOUND));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return ResLoginDto.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    public Page<ResGetAllDto> getAllUsers(int page, int size, String sortBy, boolean isAsc, String role) {

        if (!"ADMIN".equals(role)) {
            throw new BussinessException(BaseErrorCode.ACCESS_DENIED);
        }

        if (size != 10 && size != 30 && size != 50) size = 10;
        if (!"modifiedAt".equals(sortBy)) sortBy = "createdAt";

        Pageable pageable = PageRequest.of(
                Math.max(page - 1, 0),
                size,
                isAsc ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy
        );

        Page<UserEntity> users = userRepository.getUsers(pageable);

        return users.map(user -> {
            ReviewInfo review = getReviewInfo(user.getUsername());
            String address = getAddressStringSafe(user.getAddress());
            return ResGetAllDto.of(user, address, review.rating());
        });
    }

    public Object getUser(String userId, String requester, String role) {

        UserEntity user = getUserWithValidation(userId);
        ReviewInfo reviewInfo = getReviewInfo(userId);
        String address = getAddressStringSafe(user.getAddress());

        // 자기 자신 조회
        if (user.getUsername().equals(requester)) {
            return ApiResponse.success(BaseSuccessCode.OK,
                    ResGetMyInfoDto.of(user, address, reviewInfo.rating(), reviewInfo.reviews()));
        }

        // 관리자 조회
        if ("ADMIN".equals(role)) {
            return ApiResponse.success(BaseSuccessCode.OK,
                    ResGetUserAdminDto.of(user, address, reviewInfo.rating(), reviewInfo.reviews()));
        }

        // 일반유저 조회
        return ApiResponse.success(BaseSuccessCode.OK,
                ResGetUserDto.of(user, reviewInfo.rating(), reviewInfo.reviews()));
    }


    public ResGetMyInfoDto getMyInfo(String username) {

        UserEntity me = getUserWithValidation(username);
        ReviewInfo review = getReviewInfo(username);
        String address = getAddressStringSafe(me.getAddress());

        return ResGetMyInfoDto.of(me, address, review.rating(), review.reviews());
    }

    @Transactional
    public void updateUser(ReqUpdateDto dto, String username) {

        UserEntity me = userRepository.findByUsername(username)
                .orElseThrow(() -> new BussinessException(UserErrorCode.USER_NOT_FOUND));

        me.update(dto);
        me.setUpdated(Instant.now(), username);
    }

    @Transactional
    public void deleteUser(String userId, String requester, String role) {

        if (!userId.equals(requester) && !"ADMIN".equals(role)) {
            throw new BussinessException(BaseErrorCode.ACCESS_DENIED);
        }

        UserEntity user = getUserWithValidation(userId);
        user.softDelete(Instant.now(), requester);
    }

    //internal api
    public ResGetUserIntDto getUserInternal(String userId) {
        UserEntity user = userRepository.findByUsername(userId)
                .orElseThrow(() -> new BussinessException(UserErrorCode.USER_NOT_FOUND));

        String address = getAddressStringSafe(user.getAddress());

        return ResGetUserIntDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .address(address)
                .slackId(user.getSlackId())
                .role(user.getRole().name())
                .build();
    }


    public boolean existsUser(String userId) {
        return userRepository.findByUsername(userId).isPresent();
    }

    //helper
    // 유저 유효성 검사
    private UserEntity getUserWithValidation(String username) {

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BussinessException(UserErrorCode.USER_NOT_FOUND));

        if (user.getDeletedAt() != null) {
            throw new BussinessException(UserErrorCode.CANNOT_DELETE_DELETED_USER);
        }

        return user;
    }


    // 주소 처리
    private String getAddressStringSafe(UUID addressId) {

        if (addressId == null) return null;

        return addressRepository.findByAddressId(addressId)
                .map(this::formatAddressString)
                .orElse(null);
    }

    private String formatAddressString(AddressEntity address) {
        return address.getAddress() + " " + address.getDetail();
    }


    //리뷰 정보 조회
    private ReviewInfo getReviewInfo(String userId) {

        List<GetReviewIntDto> reviews = reviewServiceClient.getReviewByUser(userId);
        double rating = reviewServiceClient.getUserRating(userId);

        return new ReviewInfo(reviews, rating);
    }

    private record ReviewInfo(List<GetReviewIntDto> reviews, double rating) {
    }
}

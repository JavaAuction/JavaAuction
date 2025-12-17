package com.javaauction.user.application.service;

import com.javaauction.global.presentation.exception.BussinessException;
import com.javaauction.user.application.dto.CachedUserDto;
import com.javaauction.user.domain.entity.UserEntity;
import com.javaauction.user.domain.repository.AddressRepository;
import com.javaauction.user.domain.repository.UserRepository;
import com.javaauction.user.presentation.advice.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCacheService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Cacheable(value = "user", key = "'dto_' + #username", unless = "#result == null")
    public CachedUserDto getCachedUserDto(String username) {
        // 이 로그가 출력되면 캐시 미스 (DB 조회)
        log.warn("❌❌❌ CACHE MISS → DB 조회 시작: username={}, cacheKey=user::dto_{}", username, username);

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BussinessException(UserErrorCode.USER_NOT_FOUND));

        if (user.getDeletedAt() != null) {
            throw new BussinessException(UserErrorCode.CANNOT_DELETE_DELETED_USER);
        }

        String address = null;
        if (user.getAddress() != null) {
            address = addressRepository.findByAddressId(user.getAddress())
                    .map(a -> a.getAddress() + " " + a.getDetail())
                    .orElse(null);
        }

        CachedUserDto result = CachedUserDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .slackId(user.getSlackId())
                .role(user.getRole().name())
                .address(address)
                .build();
        
        log.info("📌 CACHE MISS → DB 조회 완료: username={}, 결과를 캐시에 저장합니다", username);
        return result;
    }
}

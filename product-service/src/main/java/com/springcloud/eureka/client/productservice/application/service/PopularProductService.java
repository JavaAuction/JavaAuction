package com.springcloud.eureka.client.productservice.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springcloud.eureka.client.productservice.domain.entity.Product;
import com.springcloud.eureka.client.productservice.infrastructure.client.PopularProductConfig;
import com.springcloud.eureka.client.productservice.infrastructure.repository.ProductRepository;
import com.springcloud.eureka.client.productservice.presentation.dto.RepProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopularProductService {

    private static final String POPULAR_PRODUCTS_KEY = "popular:products:top";

    private final ProductViewService productViewService;
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PopularProductConfig config;
    private final ObjectMapper objectMapper;

    /**
     * 인기 상품 목록 갱신
     */
    @Transactional(readOnly = true)
    public void refreshPopularProducts() {
        // 1. 상위 N개 상품 ID 조회
        Set<String> topProductIds = productViewService.getTopProductIds(config.getTopCount());

        if (topProductIds == null || topProductIds.isEmpty()) {
            log.warn("인기 상품이 없습니다.");
            return;
        }

        // 2. UUID로 변환
        List<UUID> productUUIDs = topProductIds.stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());

        // 3. DB에서 상품 정보 조회
        List<Product> products = productRepository.findAllById(productUUIDs);

        // 4. DTO 변환 + viewCount 추가 ⭐
        List<RepProductDto> productDtos = products.stream()
                .map(product -> {
                    RepProductDto dto = RepProductDto.from(product);
                    // Redis에서 조회수 가져와서 설정
                    Double viewCount = productViewService.getViewCount(product.getId());
                    dto.setViewCount(viewCount != null ? viewCount.longValue() : 0L);
                    return dto;
                })
                .collect(Collectors.toList());

        // 5. Redis에 캐시 저장
        redisTemplate.opsForValue().set(
                POPULAR_PRODUCTS_KEY,
                productDtos,
                config.getCacheTtlMinutes(),
                TimeUnit.MINUTES
        );

        log.info("=== 인기 상품 {}개 캐시 저장 완료 ===", productDtos.size());
    }

    /**
     * 인기 상품 목록 조회
     */
    public List<RepProductDto> getPopularProducts() {
        try {
            Object cachedData = redisTemplate.opsForValue().get(POPULAR_PRODUCTS_KEY);

            if (cachedData == null) {
                log.warn("인기 상품 캐시가 비어있습니다. 갱신 시작...");
                refreshPopularProducts();
                cachedData = redisTemplate.opsForValue().get(POPULAR_PRODUCTS_KEY);
            }

            if (cachedData == null) {
                return List.of();
            }

            return objectMapper.convertValue(
                    cachedData,
                    new TypeReference<List<RepProductDto>>() {}
            );

        } catch (Exception e) {
            log.error("인기 상품 조회 실패", e);
            return List.of();
        }
    }
}

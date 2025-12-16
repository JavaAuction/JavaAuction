package com.javaauction.user.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * Spring Data Redis 4.0+ 호환을 위한 커스텀 JSON 직렬화기
 * Jackson2JsonRedisSerializer의 대체 구현
 * 타입 정보를 포함하여 역직렬화 성능 최적화
 */
public class JsonRedisSerializer implements RedisSerializer<Object> {

    private final ObjectMapper objectMapper;

    public JsonRedisSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(Object value) throws SerializationException {
        if (value == null) {
            return new byte[0];
        }
        try {
            return objectMapper.writeValueAsBytes(value);
        } catch (Exception e) {
            throw new SerializationException("직렬화 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            // ObjectMapper에 타입 정보가 포함되어 있으면 정확한 타입으로 역직렬화됨
            // activateDefaultTyping이 설정되어 있으면 LinkedHashMap이 아닌 원래 타입으로 복원
            return objectMapper.readValue(bytes, Object.class);
        } catch (Exception e) {
            throw new SerializationException("역직렬화 실패: " + e.getMessage(), e);
        }
    }
}


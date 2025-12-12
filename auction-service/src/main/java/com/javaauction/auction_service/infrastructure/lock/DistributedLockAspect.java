package com.javaauction.auction_service.infrastructure.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE) // 트랜잭션보다 먼저 실행되도록
public class DistributedLockAspect {

    private final RedissonClient redissonClient;

    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(com.javaauction.auction_service.infrastructure.lock.DistributedLock)")
    public Object aroundDistributedLock(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String keyExpression = distributedLock.key();
        String prefix = distributedLock.prefix();
        long waitTime = distributedLock.waitTime();
        long leaseTime = distributedLock.leaseTime();
        TimeUnit timeUnit = distributedLock.timeUnit();

        String dynamicKey = parseSpELKey(keyExpression, signature, joinPoint.getArgs());
        String lockKey = prefix + ":" + dynamicKey;

        RLock lock = redissonClient.getLock(lockKey);

        boolean acquired = false;
        try {
            log.debug("[DistributedLock] Trying lock. key={}, waitTime={}, leaseTime={}",
                    lockKey, waitTime, leaseTime);

            acquired = lock.tryLock(waitTime, leaseTime, timeUnit);

            if (!acquired) {
                log.warn("[DistributedLock] Failed to acquire lock. key={}", lockKey);
                throw new IllegalStateException("FAILED_TO_ACQUIRE_DISTRIBUTED_LOCK");
            }

            log.debug("[DistributedLock] Lock acquired. key={}", lockKey);
            return joinPoint.proceed();

        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                try {
                    lock.unlock();
                    log.debug("[DistributedLock] Lock released. key={}", lockKey);
                } catch (IllegalMonitorStateException e) {
                    log.warn("[DistributedLock] Tried to unlock but not held by current thread. key={}", lockKey);
                }
            }
        }
    }

    /**
     * 메서드 파라미터 + SpEL 로 key 계산
     */
    private String parseSpELKey(String keyExpression, MethodSignature signature, Object[] args) {
        String[] paramNames = signature.getParameterNames();
        EvaluationContext context = new StandardEvaluationContext();

        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }

        Object value = parser.parseExpression(keyExpression).getValue(context);
        return String.valueOf(value);
    }
}

package com.javaauction.auction_service.infrastructure.config.check;

import com.javaauction.auction_service.presentation.advice.AuctionErrorCode;
import com.javaauction.global.presentation.exception.BussinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AdminOnlyAspect {

    @Around("@annotation(IsAdmin)")
    public Object checkAdmin(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attrs =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs == null) {
            throw new IllegalStateException("헤더값이 필요합니다.");
        }

        HttpServletRequest request = attrs.getRequest();
        String role = request.getHeader("X-User-Role");

        if (role == null || !role.equalsIgnoreCase("ADMIN")) {
            throw new BussinessException(AuctionErrorCode.AUCTION_FORBIDDEN_ERROR);
        }

        return joinPoint.proceed();
    }
}

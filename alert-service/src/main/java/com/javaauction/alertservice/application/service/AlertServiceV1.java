package com.javaauction.alertservice.application.service;

import com.javaauction.alertservice.domain.entity.Alert;
import com.javaauction.alertservice.domain.enums.AlertType;
import com.javaauction.alertservice.domain.repository.AlertRepository;
import com.javaauction.alertservice.infrastructure.repository.AlertJpaRepository;
import com.javaauction.alertservice.presentation.advice.AlertErrorCode;
import com.javaauction.alertservice.presentation.dto.request.ReqPostInternalAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepPostInternalAlertsDtoV1;
import com.javaauction.global.presentation.exception.BussinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertServiceV1 {
    private final AlertJpaRepository alertRepository;

    // todo : 유저 정보 받아오기
    String userId = "tmpuser1";

    // 알림 생성
    @Transactional
    public RepPostInternalAlertsDtoV1 postInternalAlertsDtoV1(ReqPostInternalAlertsDtoV1 reqDto) {
        // todo : 상품 존재 여부 확인

        String message = "";

        // todo : 메시지에 상품명, 입찰가 등 정보 표기
        switch (reqDto.getAlertType()) {
            case BID:
                message = "상품에 새로운 입찰이 발생했습니다.";
                break;
            case SUCCESS:
                message = "상품이 낙찰되었습니다.";
                break;
            case FAIL:
                message = "상품이 유찰되었습니다.";
                break;
        }

        Alert alert = Alert.ofNewAlert(
                userId,
                reqDto.getProductId(),
                reqDto.getAlertType(),
                message
                );

        alertRepository.save(alert);

        return new RepPostInternalAlertsDtoV1(
                alert.getAlertId(),
                userId,
                alert.getProductId(),
                alert.getAlertType(),
                alert.getContent(),
                alert.isRead(),
                alert.getCreatedAt());
    }

}

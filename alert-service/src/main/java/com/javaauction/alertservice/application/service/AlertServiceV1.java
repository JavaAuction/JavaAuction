package com.javaauction.alertservice.application.service;

import com.javaauction.alertservice.domain.entity.Alert;
import com.javaauction.alertservice.infrastructure.repository.AlertJpaRepository;
import com.javaauction.alertservice.presentation.dto.common.SearchParam;
import com.javaauction.alertservice.presentation.dto.request.ReqPostInternalAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepGetAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepPostInternalAlertsDtoV1;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // todo : 경매 존재 여부 확인

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
                reqDto.getAuctionId(),
                reqDto.getAlertType(),
                message
                );

        alertRepository.save(alert);

        return new RepPostInternalAlertsDtoV1(
                alert.getAlertId(),
                userId,
                alert.getAuctionId(),
                alert.getAlertType(),
                alert.getContent(),
                alert.getIsRead(),
                alert.getCreatedAt());
    }


    // 알림 리스트 조회
    public Page<RepGetAlertsDtoV1> getAlerts(SearchParam searchParam, Pageable pagable, String userId, String role) {
        Page<RepGetAlertsDtoV1> page = alertRepository.findAlertPage(searchParam, pagable, userId, role);

        // todo : 알림의 경매 ID로 상품 이름 가져오기

        return page;
    }
}



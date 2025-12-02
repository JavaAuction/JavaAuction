package com.javaauction.alertservice.application.service;

import com.javaauction.alertservice.domain.entity.Alert;
import com.javaauction.alertservice.infrastructure.repository.AlertJpaRepository;
import com.javaauction.alertservice.presentation.advice.AlertErrorCode;
import com.javaauction.alertservice.presentation.dto.common.SearchParam;
import com.javaauction.alertservice.presentation.dto.request.ReqDeleteAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.request.ReqPostInternalAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepDeleteAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepGetAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepPostAlertsReadDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepPostInternalAlertsDtoV1;
import com.javaauction.global.presentation.exception.BussinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

    // 알림 읽음 처리
    @Transactional
    public RepPostAlertsReadDtoV1 postAlertsRead(UUID alertId, String userId, String role) {

        // 알림 존재 여부 확인
        Alert alert = alertRepository.findByAlertIdAndDeletedAtIsNull(alertId)
                .orElseThrow(() -> new BussinessException(AlertErrorCode.ALERT_NOT_FOUND));


        // 권한 체크
        if (!hasPermission(userId, role, alert)) {
            throw new BussinessException(AlertErrorCode.ALERT_UNAUTH);
        }

        alert.alertRead();

        return new RepPostAlertsReadDtoV1(
                alertId,
                alert.getIsRead(),
                alert.getUpdatedAt()
        );

    }


    // 알림 삭제
    @Transactional
    public RepDeleteAlertsDtoV1 deleteAlerts(ReqDeleteAlertsDtoV1 request, String userId, String role) {

        // 삭제 대상 조회
        List<Alert> alerts = alertRepository.findAllByAlertIdInAndDeletedAtIsNull(request.getAlertIds());

        // 요청한 개수와 조회된 개수가 다를 시 예외 처리
        if(alerts.size() != request.getAlertIds().size()){
            throw new BussinessException(AlertErrorCode.ALERT_NOT_FOUND);
        }

        // 권한 체크
        alerts.forEach(alert -> {
            if (!hasPermission(userId, role, alert)) {
                throw new BussinessException(AlertErrorCode.ALERT_UNAUTH);
            }

            alert.softDelete(Instant.now(), userId);
        });

        // 삭제된 ID만 리스트로 변환
        List<UUID> deletedIds = alerts.stream()
                .map(Alert::getAlertId)
                .toList();

        // 메시지 생성
        String message = deletedIds.isEmpty()
                ? "삭제할 알림을 찾을 수 없습니다."
                : deletedIds.size() + "건의 알림이 삭제되었습니다.";

        return RepDeleteAlertsDtoV1.builder()
                .alertIds(deletedIds)
                .message(message)
                .build();
    }

    // 권한 체크
    private boolean hasPermission(String userId, String role, Alert alert) {
        boolean isAdmin = "ADMIN".equals(role);
        boolean isOwner = Objects.equals(userId, alert.getUserId());

        return isAdmin || isOwner;
    }

}



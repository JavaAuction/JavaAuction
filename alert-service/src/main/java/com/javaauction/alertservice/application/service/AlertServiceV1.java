package com.javaauction.alertservice.application.service;

import com.javaauction.alertservice.application.client.SlackClientV1;
import com.javaauction.alertservice.application.client.UserClientV1;
import com.javaauction.alertservice.domain.entity.Alert;
import com.javaauction.alertservice.infrastructure.repository.AlertJpaRepository;
import com.javaauction.alertservice.presentation.advice.AlertErrorCode;
import com.javaauction.alertservice.presentation.dto.common.SearchParam;
import com.javaauction.alertservice.presentation.dto.request.ReqDeleteAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.request.ReqPostInternalAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.*;
import com.javaauction.global.presentation.exception.BussinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertServiceV1 {
    private final AlertJpaRepository alertRepository;
    private final UserClientV1 userClient;
    private final SlackClientV1 slackClient;

    @Value("${slack.bot.token}")
    private String botToken;

    // (내부 API) 알림 생성
    @Transactional
    public RepPostInternalAlertsDtoV1 postInternalAlerts(ReqPostInternalAlertsDtoV1 reqDto) {

        // 1. 알림 DB 저장
        Alert alert = Alert.ofNewAlert(
                reqDto.getUserId(),
                reqDto.getAuctionId(),
                reqDto.getAlertType(),
                reqDto.getContent()
        );
        alertRepository.save(alert);

        // 2. Slack 발송
        try {
            RepGetInternalUsersDtoV1 repUserDto = userClient.getUser(reqDto.getUserId());

            if (repUserDto.getSlackId() != null && !repUserDto.getSlackId().isBlank()) {

                // DM 채널 열기
                Map<String, Object> openResp = slackClient.openConversation(
                        "Bearer " + botToken,
                        Map.of("users", repUserDto.getSlackId())
                );

                if (openResp != null && Boolean.TRUE.equals(openResp.get("ok"))) {
                    Map<String, Object> channelMap = (Map<String, Object>) openResp.get("channel");
                    String channelId = (String) channelMap.get("id");

                    if (channelId != null && !channelId.isBlank()) {
                        // 메시지 전송
                        Map<String, Object> msgResp = slackClient.postMessage(
                                "Bearer " + botToken,
                                Map.of("channel", channelId, "text", reqDto.getContent())
                        );

                        if (msgResp == null || !Boolean.TRUE.equals(msgResp.get("ok"))) {
                            log.warn("Slack 메시지 전송 실패: {}", msgResp != null ? msgResp.get("error") : "response null");
                        }
                    } else {
                        log.warn("Slack DM 채널 ID 획득 실패");
                    }
                } else {
                    log.warn("Slack DM 열기 실패: {}", openResp != null ? openResp.get("error") : "response null");
                }
            }
        } catch (Exception e) {
            log.error("Slack 메시지 발송 중 예외 발생", e);
        }

        // 3. 응답 반환
        return new RepPostInternalAlertsDtoV1(
                alert.getAlertId(),
                alert.getUserId(),
                alert.getAuctionId(),
                alert.getAlertType(),
                alert.getContent(),
                alert.getIsRead(),
                alert.getCreatedAt()
        );
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



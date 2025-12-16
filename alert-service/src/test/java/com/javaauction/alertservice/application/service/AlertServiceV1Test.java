package com.javaauction.alertservice.application.service;

import com.javaauction.alertservice.application.event.AlertCreatedEvent;
import com.javaauction.alertservice.domain.entity.Alert;
import com.javaauction.alertservice.domain.enums.AlertType;
import com.javaauction.alertservice.infrastructure.repository.AlertJpaRepository;
import com.javaauction.alertservice.presentation.advice.AlertErrorCode;
import com.javaauction.alertservice.presentation.dto.request.ReqDeleteAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.request.ReqPostInternalAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepDeleteAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepPostAlertsReadDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepPostInternalAlertsDtoV1;
import com.javaauction.global.presentation.exception.BussinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceV1Test {

    @InjectMocks
    private AlertServiceV1 alertService;

    @Mock
    private AlertJpaRepository alertRepository;

    @Mock
    private ApplicationEventPublisher publisher;


    @Test
    @DisplayName("내부 알림 생성 성공 - 알림 저장 및 이벤트 발행")
    void postInternalAlerts_success() {
        // given
        String userId = "user1";
        UUID auctionId = UUID.randomUUID();

        ReqPostInternalAlertsDtoV1 reqDto =
                new ReqPostInternalAlertsDtoV1(
                        auctionId,
                        userId,
                        AlertType.BID,
                        "입찰이 발생했습니다"
                );

        Alert savedAlert = Alert.ofNewAlert(
                userId,
                auctionId,
                AlertType.BID,
                "입찰이 발생했습니다"
        );

        when(alertRepository.save(any(Alert.class)))
                .thenReturn(savedAlert);

        // when
        RepPostInternalAlertsDtoV1 result =
                alertService.postInternalAlerts(reqDto);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getAuctionId()).isEqualTo(auctionId);
        assertThat(result.getAlertType()).isEqualTo(AlertType.BID);

        verify(alertRepository, times(1)).save(any(Alert.class));
        verify(publisher, times(1))
                .publishEvent(any(AlertCreatedEvent.class));
    }

    @Test
    @DisplayName("알림 읽음 처리 성공 - 본인(USER)")
    void postAlertsRead_success_owner() {
        // given
        UUID alertId = UUID.randomUUID();
        String userId = "user1";
        String role = "USER";

        Alert alert = Alert.ofNewAlert(
                userId,
                UUID.randomUUID(),
                AlertType.SUCCESS,
                "낙찰되었습니다."
        );

        when(alertRepository.findByAlertIdAndDeletedAtIsNull(alertId))
                .thenReturn(Optional.of(alert));

        // when
        RepPostAlertsReadDtoV1 result =
                alertService.postAlertsRead(alertId, userId, role);

        // then
        assertThat(result.getAlertId()).isEqualTo(alert.getAlertId());
        assertThat(alert.getIsRead()).isTrue();
    }

    @Test
    @DisplayName("알림 읽음 처리 실패 - 권한 없음")
    void postAlertsRead_forbidden() {
        // given
        UUID alertId = UUID.randomUUID();
        String userId = "user1";

        Alert alert = Alert.ofNewAlert(
                userId,
                UUID.randomUUID(),
                AlertType.SUCCESS,
                "낙찰되었습니다."
        );

        when(alertRepository.findByAlertIdAndDeletedAtIsNull(alertId))
                .thenReturn(Optional.of(alert));

        // when & then
        assertThatThrownBy(() ->
                alertService.postAlertsRead(alertId, "otherUser", "USER")
        ).isInstanceOf(BussinessException.class)
                .hasMessageContaining(AlertErrorCode.ALERT_FORBIDDEN.getMessage());
    }

    @Test
    @DisplayName("알림 삭제 성공 - 본인(USER)")
    void deleteAlerts_success() {
        // given
        UUID alertId = UUID.randomUUID();
        String userId = "user1";
        String role = "USER";

        Alert alert = Alert.ofNewAlert(
                userId,
                UUID.randomUUID(),
                AlertType.SUCCESS,
                "낙찰되었습니다."
        );

        ReflectionTestUtils.setField(alert, "alertId", alertId);

        ReqDeleteAlertsDtoV1 request =
                new ReqDeleteAlertsDtoV1(List.of(alertId));

        when(alertRepository.findAllByAlertIdInAndDeletedAtIsNull(any()))
                .thenReturn(List.of(alert));

        // when
        RepDeleteAlertsDtoV1 result =
                alertService.deleteAlerts(request, userId, role);

        // then
        assertThat(result.getAlertIds()).containsExactly(alertId);
        assertThat(alert.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("알림 삭제 실패 - 삭제 ID 없음")
    void deleteAlerts_emptyIds() {
        // given
        ReqDeleteAlertsDtoV1 request =
                new ReqDeleteAlertsDtoV1(List.of());

        // when & then
        assertThatThrownBy(() ->
                alertService.deleteAlerts(request, "user1", "USER")
        ).isInstanceOf(BussinessException.class)
                .hasMessageContaining(AlertErrorCode.ALERT_DELETE_IDS_EMPTY.getMessage());
    }

    @Test
    @DisplayName("알림 삭제 실패 - 일부 ID 없음")
    void deleteAlerts_notFound() {
        // given
        UUID alertId = UUID.randomUUID();

        ReqDeleteAlertsDtoV1 request =
                new ReqDeleteAlertsDtoV1(List.of(alertId));

        when(alertRepository.findAllByAlertIdInAndDeletedAtIsNull(any()))
                .thenReturn(List.of());

        // when & then
        assertThatThrownBy(() ->
                alertService.deleteAlerts(request, "user1", "USER")
        ).isInstanceOf(BussinessException.class)
                .hasMessageContaining(AlertErrorCode.ALERT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("알림 삭제 실패 - 권한 없음")
    void deleteAlerts_forbidden() {
        // given
        UUID alertId = UUID.randomUUID();
        String userId = "user1";

        Alert alert = Alert.ofNewAlert(
                userId,
                UUID.randomUUID(),
                AlertType.SUCCESS,
                "낙찰되었습니다."
        );



        ReqDeleteAlertsDtoV1 request =
                new ReqDeleteAlertsDtoV1(List.of(alertId));

        when(alertRepository.findAllByAlertIdInAndDeletedAtIsNull(any()))
                .thenReturn(List.of(alert));

        // when & then
        assertThatThrownBy(() ->
                alertService.deleteAlerts(request, "otherUser", "USER")
        ).isInstanceOf(BussinessException.class)
                .hasMessageContaining(AlertErrorCode.ALERT_FORBIDDEN.getMessage());
    }
}

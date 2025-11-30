package com.javaauction.alertservice.presentation.controller;

import com.javaauction.alertservice.domain.enums.AlertType;
import com.javaauction.alertservice.presentation.dto.request.ReqDeleteAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepDeleteAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepGetAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepPostAlertsReadDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepPostInternalAlertsDtoV1;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/alerts")
public class AlertControllerV1 {
    // 알림 리스트 조회
    @GetMapping
    public ResponseEntity<Page<RepGetAlertsDtoV1>> getAlerts(
            @PageableDefault(size = 10) Pageable pageable
    ) {

        RepGetAlertsDtoV1 response = new RepGetAlertsDtoV1(
                UUID.randomUUID(),
                "샘플 상품명",
                AlertType.BID,
                "입찰이 등록되었습니다.",
                false,
                Instant.now()
        );
        List<RepGetAlertsDtoV1> alerts = List.of(response);
        Page<RepGetAlertsDtoV1> alertsDtoV1Page = new PageImpl<>(alerts, pageable, 0);

        return ResponseEntity.ok(alertsDtoV1Page);
    }

    // 채팅 읽음 처리
    @PostMapping("/{alertId}/read")
    public ResponseEntity<RepPostAlertsReadDtoV1> readAlert(@PathVariable UUID alertId) {
        RepPostAlertsReadDtoV1 response = new RepPostAlertsReadDtoV1(
                alertId,
                true,
                Instant.now()
        );

        return ResponseEntity.ok(response);
    }

    // 업체 삭제
    @DeleteMapping
    public ResponseEntity<RepDeleteAlertsDtoV1> deleteVendor(@RequestBody ReqDeleteAlertsDtoV1 request) {

        RepDeleteAlertsDtoV1 response = RepDeleteAlertsDtoV1.builder()
                .alertIds(request.getAlertIds())
                .message(request.getAlertIds().size() + "건 알림 삭제에 성공했습니다.")
                .build();

        return ResponseEntity.ok(response);
    }

}

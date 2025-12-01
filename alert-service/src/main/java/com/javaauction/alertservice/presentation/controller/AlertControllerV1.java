package com.javaauction.alertservice.presentation.controller;

import com.javaauction.alertservice.application.service.AlertServiceV1;
import com.javaauction.alertservice.domain.enums.AlertType;
import com.javaauction.alertservice.presentation.advice.AlertSuccessCode;
import com.javaauction.alertservice.presentation.dto.common.SearchParam;
import com.javaauction.alertservice.presentation.dto.request.ReqDeleteAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepDeleteAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepGetAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepPostAlertsReadDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepPostInternalAlertsDtoV1;
import com.javaauction.global.presentation.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
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
    private final AlertServiceV1 alertServiceV1;

    // 알림 리스트 조회
    @GetMapping
    public ResponseEntity<Page<RepGetAlertsDtoV1>> getAlerts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) AlertType alertType,
            @RequestParam(required = false) Boolean isRead,
            @PageableDefault(size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "isRead", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            }) Pageable pageable
    ) {

        SearchParam searchParam = new SearchParam(search, alertType, isRead);
        Page<RepGetAlertsDtoV1> getAlertsDto = alertServiceV1.getAlerts(searchParam, pageable, "tmpuser1", "USER"); // 임시로 userid, role 설정

        return ResponseEntity.ok(ApiResponse.success(AlertSuccessCode.ALERT_FIND_SUCCESS, getAlertsDto).getData());
    }

    // 알림 읽음 처리
    @PostMapping("/{alertId}/read")
    public ResponseEntity<RepPostAlertsReadDtoV1> readAlert(@PathVariable UUID alertId) {
        RepPostAlertsReadDtoV1 response = new RepPostAlertsReadDtoV1(
                alertId,
                true,
                Instant.now()
        );

        return ResponseEntity.ok(response);
    }

    // 알림 삭제
    @DeleteMapping
    public ResponseEntity<RepDeleteAlertsDtoV1> deleteVendor(@RequestBody ReqDeleteAlertsDtoV1 request) {

        RepDeleteAlertsDtoV1 response = RepDeleteAlertsDtoV1.builder()
                .alertIds(request.getAlertIds())
                .message(request.getAlertIds().size() + "건 알림 삭제에 성공했습니다.")
                .build();

        return ResponseEntity.ok(response);
    }

}

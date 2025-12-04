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
    public ResponseEntity<ApiResponse<Page<RepGetAlertsDtoV1>>> getAlerts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) AlertType alertType,
            @RequestParam(required = false) Boolean isRead,
            @PageableDefault(size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "isRead", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            }) Pageable pageable,
            @RequestHeader("X-User-Username") String username,
            @RequestHeader("X-User-Role") String role
    ) {

        SearchParam searchParam = new SearchParam(search, alertType, isRead);
        Page<RepGetAlertsDtoV1> getAlertsDto = alertServiceV1.getAlerts(searchParam, pageable, username, role);

        return ResponseEntity.ok(
                ApiResponse.success(AlertSuccessCode.ALERT_FIND_SUCCESS, getAlertsDto)
        );
    }

    // 알림 읽음 처리
    @PostMapping("/{alertId}/read")
    public ResponseEntity<ApiResponse<RepPostAlertsReadDtoV1>> readAlert(@PathVariable UUID alertId,
                                                                         @RequestHeader("X-User-Username") String username,
                                                                         @RequestHeader("X-User-Role") String role) {
        RepPostAlertsReadDtoV1 postAlertsReadDto = alertServiceV1.postAlertsRead(alertId,  username, role);
        return ResponseEntity.ok(
                ApiResponse.success(AlertSuccessCode.ALERT_FIND_SUCCESS, postAlertsReadDto)
        );
    }

    // 알림 삭제
    @DeleteMapping
    public ResponseEntity<ApiResponse<RepDeleteAlertsDtoV1>> deleteAlerts(
            @RequestBody ReqDeleteAlertsDtoV1 request,
            @RequestHeader("X-User-Username") String username,
            @RequestHeader("X-User-Role") String role
    ) {

        RepDeleteAlertsDtoV1 response = alertServiceV1.deleteAlerts(request, username, role);

        return ResponseEntity.ok(
                ApiResponse.success(AlertSuccessCode.ALERT_FIND_SUCCESS, response)
        );
    }

}

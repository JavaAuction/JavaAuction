package com.javaauction.alertservice.presentation.controller;

import com.javaauction.alertservice.application.service.AlertServiceV1;
import com.javaauction.alertservice.domain.enums.AlertType;
import com.javaauction.alertservice.presentation.advice.AlertSuccessCode;
import com.javaauction.alertservice.presentation.dto.common.SearchParam;
import com.javaauction.alertservice.presentation.dto.request.ReqDeleteAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepDeleteAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepGetAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepPostAlertsReadDtoV1;
import com.javaauction.global.presentation.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(
        name = "알림 컨트롤러",
        description = "사용자에게 전달되는 알림을 조회하고 관리하는 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/alerts")
public class AlertControllerV1 {
    private final AlertServiceV1 alertServiceV1;

    @Operation(summary = "알림 목록 조회",
            description = """
            로그인한 사용자의 알림 목록을 조회합니다.
            검색어, 알림 타입, 읽음 여부를 기준으로 필터링할 수 있으며
            기본적으로 읽지 않은 알림을 우선하여 최신순으로 정렬됩니다.
            """)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RepGetAlertsDtoV1>>> getAlerts(
            @RequestParam(required = false) String content,
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

        SearchParam searchParam = new SearchParam(content, alertType, isRead);
        Page<RepGetAlertsDtoV1> getAlertsDto =
                alertServiceV1.getAlerts(searchParam, pageable, username, role);

        return ResponseEntity.ok(
                ApiResponse.success(AlertSuccessCode.ALERT_FIND_SUCCESS, getAlertsDto)
        );
    }

    @Operation(summary = "알림 읽음 처리",
            description = """
            특정 알림을 읽음 상태로 변경합니다.
            이미 읽은 알림을 다시 요청하더라도 상태는 유지됩니다.
            """)
    @PatchMapping("/{alertId}/read")
    public ResponseEntity<ApiResponse<RepPostAlertsReadDtoV1>> readAlert(
            @PathVariable UUID alertId,
            @RequestHeader("X-User-Username") String username,
            @RequestHeader("X-User-Role") String role
    ) {

        RepPostAlertsReadDtoV1 postAlertsReadDto =
                alertServiceV1.postAlertsRead(alertId, username, role);

        return ResponseEntity.ok(
                ApiResponse.success(AlertSuccessCode.ALERT_FIND_SUCCESS, postAlertsReadDto)
        );
    }

    @Operation(summary = "알림 삭제",
            description = """
            사용자가 선택한 여러 개의 알림을 삭제합니다.
            삭제된 알림은 복구할 수 없습니다.
            """)
    @DeleteMapping
    public ResponseEntity<ApiResponse<RepDeleteAlertsDtoV1>> deleteAlerts(
            @RequestBody ReqDeleteAlertsDtoV1 request,
            @RequestHeader("X-User-Username") String username,
            @RequestHeader("X-User-Role") String role
    ) {

        RepDeleteAlertsDtoV1 response =
                alertServiceV1.deleteAlerts(request, username, role);

        return ResponseEntity.ok(
                ApiResponse.success(AlertSuccessCode.ALERT_FIND_SUCCESS, response)
        );
    }
}


package com.javaauction.alertservice.infrastructure.repository;

import com.javaauction.alertservice.domain.entity.Alert;
import com.javaauction.alertservice.presentation.dto.common.SearchParam;
import com.javaauction.alertservice.presentation.dto.response.RepGetAlertsDtoV1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface AlertRepository {
    // 검색 조건, 페이지 정보 기반 알림 목록 동적 조회
    Page<RepGetAlertsDtoV1> findAlertPage(SearchParam searchParam, Pageable pageable, String userId, String role);



}

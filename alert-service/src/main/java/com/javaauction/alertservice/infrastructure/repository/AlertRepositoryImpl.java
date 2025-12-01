package com.javaauction.alertservice.infrastructure.repository;

import com.javaauction.alertservice.domain.entity.Alert;
import com.javaauction.alertservice.domain.entity.QAlert;
import com.javaauction.alertservice.presentation.dto.common.SearchParam;
import com.javaauction.alertservice.presentation.dto.response.QRepGetAlertsDtoV1;
import com.javaauction.alertservice.presentation.dto.response.RepGetAlertsDtoV1;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.OrderSpecifier;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertRepositoryImpl implements AlertRepository {

    private final JPAQueryFactory query;

    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of(
            "productName", "vendorType", "isRead", "createdAt", "updatedAt");

    QAlert qAlert = QAlert.alert;

    // 알림 리스트 조회
    @Override
    public Page<RepGetAlertsDtoV1> findAlertPage(SearchParam searchParam, Pageable pageable, String userId, String role) {
        int pageSize = pageable.getPageSize();
        List<Integer> allowedPageSizes = Arrays.asList(10, 30, 50);
        if (!allowedPageSizes.contains(pageSize)) {
            pageSize = 10;
        }

        Pageable adjustedPageable = PageRequest.of(pageable.getPageNumber(), pageSize, pageable.getSort());

        JPAQuery<RepGetAlertsDtoV1> jpaQuery = query.select(getAlertProjection())
                .from(qAlert)
                .where(whereExpression(searchParam, userId, role))
                .offset(adjustedPageable.getOffset())
                .limit(adjustedPageable.getPageSize());

        if (adjustedPageable.getSort().isSorted()) {
            for (Sort.Order order : adjustedPageable.getSort()) {
                if (!ALLOWED_SORT_PROPERTIES.contains(order.getProperty())) {
                    continue;
                }
                PathBuilder<Alert> entityPath = new PathBuilder<>(Alert.class, "alert");
                jpaQuery.orderBy(new OrderSpecifier(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        entityPath.get(order.getProperty())
                ));
            }
        } else {
            jpaQuery.orderBy(qAlert.isRead.asc(), qAlert.createdAt.desc());
        }

        JPAQuery<Long> cnt = query
                .select(qAlert.count())
                .from(qAlert)
                .where(whereExpression(searchParam, userId, role));

        List<RepGetAlertsDtoV1> results = jpaQuery.fetch();

        return PageableExecutionUtils.getPage(results, adjustedPageable, cnt::fetchOne);
    }

    private QRepGetAlertsDtoV1 getAlertProjection() {
        return new QRepGetAlertsDtoV1(
                qAlert.alertId,
                Expressions.nullExpression(), // 서비스에서 상품명 받아올 예정
                qAlert.alertType,
                qAlert.content,
                qAlert.isRead,
                qAlert.createdAt
        );
    }

    private BooleanBuilder whereExpression(SearchParam searchParam, String userId, String role) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // 일반 회원은 자신에게 발생한 알림만 조회 가능
        if ("USER".equals(role)) {
            booleanBuilder.and(qAlert.userId.eq(userId))
                    .and(qAlert.deletedAt.isNull());
        }

        // 검색 조건
        if (searchParam.getTerm() != null) {
            booleanBuilder.and(
                    qAlert.content.containsIgnoreCase(searchParam.getTerm())
            );
        }

        if (searchParam.getAlertType() != null) {
            booleanBuilder.and(qAlert.alertType.eq(searchParam.getAlertType()));
        }

        if (searchParam.getIsRead() != null) {
            booleanBuilder.and(qAlert.isRead.eq(searchParam.getIsRead()));
        }

        return booleanBuilder;
    }



}

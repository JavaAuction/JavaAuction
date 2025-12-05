package com.javaauction.chatservice.infrastructure.repository;

import com.javaauction.chatservice.domain.entity.Chatroom;
import com.javaauction.chatservice.domain.entity.QChatroom;
import com.javaauction.chatservice.domain.entity.QChatting;
import com.javaauction.chatservice.presentation.dto.common.ChatroomSearchParam;
import com.javaauction.chatservice.presentation.dto.response.QRepGetChatroomsDtoV1;
import com.javaauction.chatservice.presentation.dto.response.RepGetChatroomsDtoV1;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
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
import com.querydsl.core.types.OrderSpecifier;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatroomRepositoryImpl implements ChatroomRepository {

    private final JPAQueryFactory queryFactory;
    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of(
            "chatroomHost", "chatroomGuest", "createdAt"
    );

    QChatroom qChatroom = QChatroom.chatroom;
    QChatting qChatting = QChatting.chatting;

    // 채팅방 리스트 조회
    @Override
    public Page<RepGetChatroomsDtoV1> findChatroomPage(ChatroomSearchParam chatroomSearchParam, Pageable pageable, String userId, String role) {
        int pageSize = pageable.getPageSize();
        List<Integer> allowedPageSizes = Arrays.asList(10, 30, 50);
        if (!allowedPageSizes.contains(pageSize)) {
            pageSize = 10;
        }

        Pageable adjustedPageable = PageRequest.of(pageable.getPageNumber(), pageSize, pageable.getSort());

        JPAQuery<RepGetChatroomsDtoV1> jpaQuery = queryFactory
                .select(getChatroomProjection())
                .from(qChatroom)
                .leftJoin(qChatting)
                .on(
                        qChatting.chatroom.chatroomId.eq(qChatroom.chatroomId)
                                .and(qChatting.createdAt.eq(latestChatCreatedAtSubQuery()))
                )
                .where(whereExpression(chatroomSearchParam, userId, role))
                .orderBy(qChatroom.createdAt.desc())
                .offset(adjustedPageable.getOffset())
                .limit(adjustedPageable.getPageSize());

        if (adjustedPageable.getSort().isSorted()) {
            for (Sort.Order order : adjustedPageable.getSort()) {
                if (!ALLOWED_SORT_PROPERTIES.contains(order.getProperty())) {
                    continue;
                }
                PathBuilder<Chatroom> entityPath = new PathBuilder<>(Chatroom.class, "chatroom");
                jpaQuery.orderBy(new OrderSpecifier(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        entityPath.get(order.getProperty())
                ));
            }
        } else {
            jpaQuery.orderBy(qChatroom.createdAt.desc());
        }

        JPAQuery<Long> cnt = queryFactory
                .select(qChatroom.count())
                .from(qChatroom)
                .where(whereExpression(chatroomSearchParam, userId, role));

        List<RepGetChatroomsDtoV1> results = jpaQuery.fetch();

        return PageableExecutionUtils.getPage(results, adjustedPageable, cnt::fetchOne);
    }

    private QRepGetChatroomsDtoV1 getChatroomProjection() {
        return new QRepGetChatroomsDtoV1(
                qChatroom.chatroomId,
                qChatroom.productId,
                qChatroom.chatroomHost,
                qChatroom.chatroomGuest,
                qChatting.content,
                qChatroom.createdAt
        );
    }

    private JPQLQuery<Instant> latestChatCreatedAtSubQuery() {
        return JPAExpressions
                .select(qChatting.createdAt.max())
                .from(qChatting)
                .where(qChatting.chatroom.chatroomId.eq(qChatroom.chatroomId));
    }


    private BooleanBuilder whereExpression(ChatroomSearchParam chatroomSearchParam,
                                           String userId,
                                           String role) {

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // 일반 회원은 자신이 소속된 채팅방만
        if ("USER".equals(role)) {
            BooleanBuilder userCondition = new BooleanBuilder();
            userCondition.or(qChatroom.chatroomHost.eq(userId));
            userCondition.or(qChatroom.chatroomGuest.eq(userId));

            booleanBuilder
                    .and(qChatroom.deletedAt.isNull())
                    .and(userCondition);
        }

        // 검색 조건
        if (chatroomSearchParam.getProductId() != null) {
            booleanBuilder.and(qChatroom.productId.eq(chatroomSearchParam.getProductId()));
        }

        if (chatroomSearchParam.getChatroomHost() != null) {
            booleanBuilder.and(qChatroom.chatroomHost.eq(chatroomSearchParam.getChatroomHost()));
        }

        if (chatroomSearchParam.getChatroomGuest() != null) {
            booleanBuilder.and(qChatroom.chatroomGuest.eq(chatroomSearchParam.getChatroomGuest()));
        }

        return booleanBuilder;
    }
}

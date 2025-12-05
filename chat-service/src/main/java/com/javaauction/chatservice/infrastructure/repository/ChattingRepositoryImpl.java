package com.javaauction.chatservice.infrastructure.repository;

import com.javaauction.chatservice.domain.entity.Chatting;
import com.javaauction.chatservice.domain.entity.QChatroom;
import com.javaauction.chatservice.domain.entity.QChatting;
import com.javaauction.chatservice.presentation.dto.common.ChattingSearchParam;
import com.javaauction.chatservice.presentation.dto.response.QRepGetChatsDtoV1;
import com.javaauction.chatservice.presentation.dto.response.RepGetChatsDtoV1;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChattingRepositoryImpl implements ChattingRepository {

    private final JPAQueryFactory queryFactory;
    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of(
            "senderId", "receiverId", "isRead", "createdAt"
    );

    QChatroom qChatroom = QChatroom.chatroom;
    QChatting qChatting = QChatting.chatting;

    // 채팅 조회
    @Override
    public Page<RepGetChatsDtoV1> findChattingPage(UUID chatroomId, ChattingSearchParam chattingSearchParam, Pageable pageable, String userId, String role) {
        int pageSize = pageable.getPageSize();
        List<Integer> allowedPageSizes = Arrays.asList(10, 30, 50);
        if (!allowedPageSizes.contains(pageSize)) {
            pageSize = 10;
        }

        Pageable adjustedPageable = PageRequest.of(pageable.getPageNumber(), pageSize, pageable.getSort());

        JPAQuery<RepGetChatsDtoV1> jpaQuery = queryFactory.select(getChattingProjection())
                .from(qChatting)
                .where(whereExpression(chatroomId, chattingSearchParam, userId, role))
                .offset(adjustedPageable.getOffset())
                .limit(adjustedPageable.getPageSize());

        if (adjustedPageable.getSort().isSorted()) {
            for (Sort.Order order : adjustedPageable.getSort()) {
                if (!ALLOWED_SORT_PROPERTIES.contains(order.getProperty())) {
                    continue;
                }
                PathBuilder<Chatting> entityPath = new PathBuilder<>(Chatting.class, "chatting");
                jpaQuery.orderBy(new OrderSpecifier(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        entityPath.get(order.getProperty())
                ));
            }
        } else {
            jpaQuery.orderBy(qChatting.createdAt.desc());
        }

        JPAQuery<Long> cnt = queryFactory
                .select(qChatting.count())
                .from(qChatting)
                .where(whereExpression(chatroomId, chattingSearchParam, userId, role));

        List<RepGetChatsDtoV1> results = jpaQuery.fetch();

        return PageableExecutionUtils.getPage(results, adjustedPageable, cnt::fetchOne);

    }

    private QRepGetChatsDtoV1 getChattingProjection() {
        return new QRepGetChatsDtoV1(
                qChatting.chattingId,
                qChatting.chatroom.chatroomId,
                qChatting.senderId,
                qChatting.receiverId,
                qChatting.isRead,
                qChatting.content,
                qChatting.createdAt,
                qChatting.updatedAt
        );
    }

    private BooleanBuilder whereExpression(UUID chatroomId,
                                           ChattingSearchParam chattingSearchParam,
                                           String userId,
                                           String role) {

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        booleanBuilder.and(qChatting.chatroom.chatroomId.eq(chatroomId));

        // 일반 회원은 자신이 수신자이거나 발신자인 채팅만 조회 가능
        if ("USER".equals(role)) {
            BooleanBuilder userCondition = new BooleanBuilder();
            userCondition.or(qChatting.senderId.eq(userId));
            userCondition.or(qChatting.receiverId.eq(userId));

            booleanBuilder
                    .and(qChatroom.deletedAt.isNull())
                    .and(userCondition);
        }

        // 검색 조건
        if (chattingSearchParam.getSenderId() != null) {
            booleanBuilder.and(qChatting.senderId.eq(chattingSearchParam.getSenderId()));
        }

        if (chattingSearchParam.getReceiverId() != null) {
            booleanBuilder.and(qChatting.receiverId.eq(chattingSearchParam.getReceiverId()));
        }

        if (chattingSearchParam.getIsRead() != null) {
            booleanBuilder.and(qChatting.isRead.eq(chattingSearchParam.getIsRead()));
        }

        if (chattingSearchParam.getContent() != null) {
            booleanBuilder.and(qChatting.content.containsIgnoreCase(chattingSearchParam.getContent()));
        }


        return booleanBuilder;
    }
}

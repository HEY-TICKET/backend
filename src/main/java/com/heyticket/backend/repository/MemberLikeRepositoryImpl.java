package com.heyticket.backend.repository;

import static com.heyticket.backend.domain.QMemberLike.memberLike;
import static com.heyticket.backend.domain.QPerformance.performance;

import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.domain.enums.PerformanceStatus;
import com.heyticket.backend.service.dto.request.MemberLikeListRequest;
import com.heyticket.backend.service.enums.LikeSortType;
import com.heyticket.backend.service.enums.SortOrder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class MemberLikeRepositoryImpl implements MemberLikeCustomRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Performance> findMemberLikePerformanceByMemberEmail(MemberLikeListRequest request, Pageable pageable) {
        List<Performance> performances = queryFactory.select(performance)
            .from(memberLike)
            .where(
                memberLike.member.email.eq(request.getEmail()),
                eqPerformanceStatus(request.getStatus())
            )
            .join(performance)
            .on(memberLike.performance.eq(performance))
            .orderBy(orderCondition(request.getLikeSortType(), request.getSortOrder()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(performance.count())
            .from(memberLike)
            .where(
                memberLike.member.email.eq(request.getEmail()),
                eqPerformanceStatus(request.getStatus())
            )
            .join(performance)
            .on(memberLike.performance.eq(performance));

        return PageableExecutionUtils.getPage(performances, pageable, countQuery::fetchOne);
    }

    private Predicate eqPerformanceStatus(PerformanceStatus status) {
        if (status == null) {
            return null;
        }
        return performance.status.eq(status);
    }

    private OrderSpecifier<?> orderCondition(LikeSortType likeSortType, SortOrder sortOrder) {
        return switch (likeSortType) {
            case LIKE_DATE -> sortOrder == SortOrder.ASC ? memberLike.createdDate.asc() : memberLike.createdDate.desc();
            case CREATED_DATE -> sortOrder == SortOrder.ASC ? performance.createdDate.asc() : performance.createdDate.desc();
            default -> null;
        };
    }
}

package com.heyticket.backend.repository;


import static com.heyticket.backend.domain.QPerformance.performance;

import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.module.kopis.enums.Genre;
import com.heyticket.backend.module.kopis.enums.SortOrder;
import com.heyticket.backend.module.kopis.enums.SortType;
import com.heyticket.backend.service.dto.request.NewPerformanceRequest;
import com.heyticket.backend.service.dto.response.GenreCountResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
public class PerformanceRepositoryImpl implements PerformanceCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findAllIds() {
        return queryFactory.select(performance.id)
            .from(performance)
            .fetch();
    }

    @Override
    public Page<Performance> findNewPerformances(NewPerformanceRequest request, Pageable pageable) {
        List<Performance> performanceList = queryFactory.selectFrom(performance)
            .where(
                eqPerformanceGenre(request.getGenre()),
                performance.createdDate.goe(LocalDateTime.now().minusDays(7))
            )
            .orderBy(orderBy(request.getSortType(), request.getSortOrder()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> count = queryFactory.select(performance.count())
            .from(performance)
            .where(
                eqPerformanceGenre(request.getGenre()),
                performance.createdDate.goe(LocalDateTime.now().minusDays(7))
            );

        return PageableExecutionUtils.getPage(performanceList, pageable, count::fetchOne);
    }

    @Override
    public List<GenreCountResponse> findPerformanceGenreCount() {
        return queryFactory.select(
                Projections.fields(
                    GenreCountResponse.class,
                    performance.genre.as("genre"),
                    performance.genre.count().as("count")
                ))
            .from(performance)
            .groupBy(performance.genre)
            .fetch();
    }

    private BooleanExpression eqPerformanceGenre(Genre genre) {
        if (ObjectUtils.isEmpty(genre)) {
            return null;
        }
        return performance.genre.eq(genre.getName());
    }

    private OrderSpecifier<?> orderBy(SortType sortType, SortOrder sortOrder) {
        if (ObjectUtils.isEmpty(sortOrder)) {
            return performance.createdDate.desc();
        }

        switch (sortType) {
            case TIME:
                if (sortOrder == SortOrder.ASC) {
                    return performance.createdDate.asc();
                } else {
                    return performance.createdDate.desc();
                }
            case VIEWS:
                if (sortOrder == SortOrder.ASC) {
                    return performance.views.asc();
                } else {
                    return performance.views.desc();
                }
            default:
                return null;
        }
    }

}

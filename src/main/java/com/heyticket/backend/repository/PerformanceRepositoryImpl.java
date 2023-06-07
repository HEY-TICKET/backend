package com.heyticket.backend.repository;


import static com.heyticket.backend.domain.QPerformance.performance;
import static com.heyticket.backend.domain.QPerformancePrice.performancePrice;

import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.domain.enums.PerformancePriceLevel;
import com.heyticket.backend.domain.enums.PerformanceStatus;
import com.heyticket.backend.module.kopis.enums.Area;
import com.heyticket.backend.module.kopis.enums.Genre;
import com.heyticket.backend.module.kopis.enums.SortOrder;
import com.heyticket.backend.module.kopis.enums.SortType;
import com.heyticket.backend.service.dto.request.NewPerformanceRequest;
import com.heyticket.backend.service.dto.request.PerformanceFilterRequest;
import com.heyticket.backend.service.dto.request.PerformanceSearchRequest;
import com.heyticket.backend.service.dto.response.GenreCountResponse;
import com.heyticket.backend.service.enums.SearchType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
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
    public Page<Performance> findPerformanceByCondition(PerformanceFilterRequest request, Pageable pageable) {
        List<Performance> performanceList = queryFactory.selectFrom(performance)
            .where(
                inPrice(request.getPrice()),
                inGenres(request.getGenres()),
                inAreas(request.getAreas()),
                inStatuses(request.getStatuses()),
                afterDate(request.getDate())
            )
            .join(performancePrice)
            .on(performancePrice.performance.eq(performance))
            .orderBy(orderCondition(request.getSortType(), request.getSortOrder()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> count = queryFactory.select(performance.count())
            .from(performance)
            .where(
                inPrice(request.getPrice()),
                inGenres(request.getGenres()),
                inAreas(request.getAreas()),
                inStatuses(request.getStatuses()),
                afterDate(request.getDate())
            )
            .join(performancePrice)
            .on(performancePrice.performance.eq(performance));

        return PageableExecutionUtils.getPage(performanceList, pageable, count::fetchOne);
    }

    @Override
    public Page<Performance> findPerformanceBySearchQuery(PerformanceSearchRequest request, Pageable pageable) {
        List<Performance> performanceList = queryFactory.selectFrom(performance)
            .where(
                eqQuery(request.getSearchType(), request.getQuery())
            )
            .orderBy(performance.views.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> count = queryFactory.select(performance.count())
            .from(performance)
            .where(
                eqQuery(request.getSearchType(), request.getQuery())
            );

        return PageableExecutionUtils.getPage(performanceList, pageable, count::fetchOne);
    }

    private Predicate eqQuery(SearchType searchType, String query) {
        if (ObjectUtils.isEmpty(searchType) || searchType == SearchType.PERFORMANCE) {
            return performance.title.containsIgnoreCase(query);
        }
        return performance.title.containsIgnoreCase(query).or(performance.cast.containsIgnoreCase(query));
    }

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
            .orderBy(orderCondition(request.getSortType(), request.getSortOrder()))
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
        return (genre == null) || (genre == Genre.ALL) ? null : performance.genre.in(genre);
    }

    private Predicate afterDate(LocalDate date) {
        return date == null ? null : performance.endDate.loe(date);
    }

    private BooleanExpression inGenres(List<Genre> genres) {
        return ObjectUtils.isEmpty(genres) ? null : performance.genre.in(genres);
    }

    private BooleanExpression inAreas(List<Area> areas) {
        return ObjectUtils.isEmpty(areas) ? null : performance.area.in(areas);
    }

    private BooleanExpression inStatuses(List<PerformanceStatus> statuses) {
        return ObjectUtils.isEmpty(statuses) ? null : performance.status.in(statuses);
    }

    private Predicate inPrice(PerformancePriceLevel price) {
        return price == null ? null : performancePrice.price.goe(price.getLowPrice())
            .and(performancePrice.price.loe(price.getHighPrice()));
    }

    private OrderSpecifier<?> orderCondition(SortType sortType, SortOrder sortOrder) {
        if (sortType == null) {
            return performance.createdDate.desc();
        }

        return switch (sortType) {
            case END_DATE -> sortOrder == SortOrder.ASC ? performance.endDate.asc() : performance.endDate.desc();
            case TIME -> sortOrder == SortOrder.ASC ? performance.createdDate.asc() : performance.createdDate.desc();
            case VIEWS -> sortOrder == SortOrder.ASC ? performance.views.asc() : performance.views.desc();
        };
    }

}

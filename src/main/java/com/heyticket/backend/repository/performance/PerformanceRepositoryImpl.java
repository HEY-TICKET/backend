package com.heyticket.backend.repository.performance;


import static com.heyticket.backend.domain.QPerformance.performance;
import static com.heyticket.backend.domain.QPerformancePrice.performancePrice;
import static com.heyticket.backend.domain.QPlace.place;

import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.module.meilesearch.dto.MeiliPerformanceSaveResponse;
import com.heyticket.backend.service.dto.request.NewPerformanceRequest;
import com.heyticket.backend.service.dto.request.PerformanceFilterRequest;
import com.heyticket.backend.service.dto.response.GenreCountResponse;
import com.heyticket.backend.service.enums.Area;
import com.heyticket.backend.service.enums.Genre;
import com.heyticket.backend.service.enums.PerformanceStatus;
import com.heyticket.backend.service.enums.SearchType;
import com.heyticket.backend.service.enums.SortOrder;
import com.heyticket.backend.service.enums.SortType;
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
                inPrice(request),
                inGenres(request.getGenres()),
                inAreas(request.getAreas()),
                inStatuses(request.getStatuses()),
                afterDate(request.getDate())
            )
            .leftJoin(performancePrice)
            .on(performancePrice.performance.eq(performance))
            .orderBy(orderCondition(request.getSortType(), request.getSortOrder()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .distinct()
            .fetch();

        JPAQuery<Long> count = queryFactory.select(performance.id.countDistinct())
            .from(performance)
            .where(
                inPrice(request),
                inGenres(request.getGenres()),
                inAreas(request.getAreas()),
                inStatuses(request.getStatuses()),
                afterDate(request.getDate())
            )
            .leftJoin(performancePrice)
            .on(performancePrice.performance.eq(performance));

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

    @Override
    public List<MeiliPerformanceSaveResponse> findMeiliPerformanceSaveForms() {
        return queryFactory.select(
            Projections.fields(
                MeiliPerformanceSaveResponse.class,
                performance.id.as("id"),
                place.address.as("address"),
                performance.title.as("title"),
                performance.startDate.as("startDate"),
                performance.endDate.as("endDate"),
                performance.theater.as("theater"),
                performance.cast.as("cast"),
                performance.runtime.as("runtime"),
                performance.age.as("age"),
                performance.company.as("company"),
                performance.price.as("price"),
                performance.poster.as("poster"),
                performance.genre.as("genre"),
                performance.status.as("status"),
                performance.openRun.as("openRun"),
                performance.schedule.as("schedule"),
                performance.views.as("views"),
                place.latitude.as("latitude"),
                place.longitude.as("longitude"),
                place.phoneNumber.as("phoneNumber"),
                place.area.as("sido"),
                place.gugunName.as("gugun")
            ))
            .from(performance)
            .leftJoin(place)
            .on(performance.place.eq(place))
            .fetch();
    }

    private BooleanExpression eqPerformanceGenre(Genre genre) {
        return (genre == null) || (genre == Genre.ALL) ? null : performance.genre.in(genre);
    }

    private Predicate afterDate(LocalDate date) {
        return date == null ? null : performance.endDate.goe(date);
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

    private Predicate inPrice(PerformanceFilterRequest request) {
        if (request.getMinPrice() != null && request.getMaxPrice() == null) {
            return performancePrice.price.goe(request.getMinPrice());
        }
        if (request.getMaxPrice() != null && request.getMinPrice() == null) {
            return performancePrice.price.loe(request.getMaxPrice());
        }
        if (request.getMinPrice() != null && request.getMaxPrice() != null) {
            return performancePrice.price.loe(request.getMaxPrice())
                .and(performancePrice.price.goe(request.getMinPrice()));
        }

        return null;
    }

    private OrderSpecifier<?> orderCondition(SortType sortType, SortOrder sortOrder) {
        if (sortType == null) {
            return performance.views.desc();
        }

        return switch (sortType) {
            case CREATED_DATE -> sortOrder == SortOrder.ASC ? performance.createdDate.asc() : performance.createdDate.desc();
            case VIEWS -> sortOrder == SortOrder.ASC ? performance.views.asc() : performance.views.desc();
            default -> null;
        };
    }

}

package com.heyticket.backend.repository;


import static com.heyticket.backend.domain.QPerformance.performance;

import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.service.dto.NewPerformanceRequest;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

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
    public Page<Performance> findNewPerformances(NewPerformanceRequest newPerformanceRequest, Pageable pageable) {
        List<Performance> performanceList = queryFactory.selectFrom(performance)
            .where(performance.createdDate.goe(LocalDateTime.now().minusDays(7)))
            .orderBy(performance.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> count = queryFactory.select(performance.count())
            .from(performance)
            .where(performance.createdDate.goe(LocalDateTime.now().minusDays(7)));

        return PageableExecutionUtils.getPage(performanceList, pageable, count::fetchOne);
    }

}

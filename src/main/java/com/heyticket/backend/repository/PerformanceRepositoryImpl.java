package com.heyticket.backend.repository;


import static com.heyticket.backend.domain.QPerformance.performance;

import com.heyticket.backend.domain.Performance;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

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
    public List<Performance> findNewPerformances() {
        return queryFactory.selectFrom(performance)
            .where(performance.createdDate.goe(LocalDateTime.now().minusDays(7)))
            .orderBy(performance.createdDate.desc())
            .limit(20)
            .fetch();
    }

}

package com.heyticket.backend.repository;


import static com.heyticket.backend.kopis.domain.QPerformance.performance;

import com.heyticket.backend.kopis.domain.Performance;
import com.heyticket.backend.kopis.domain.QPerformance;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PerformanceRepositoryImpl implements PerformanceCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Performance> findById(String id) {
        Performance performance = queryFactory.selectFrom(QPerformance.performance)
            .where(QPerformance.performance.id.eq(id))
            .fetchOne();
        return Optional.ofNullable(performance);
    }

    @Override
    public List<String> findAllIds() {
        return queryFactory.select(performance.id)
            .from(performance)
            .fetch();
    }

}

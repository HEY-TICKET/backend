package com.heyticket.backend.repository;


import com.heyticket.backend.kopis.domain.Performance;
import com.heyticket.backend.kopis.domain.QPerformance;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PerformanceRepositoryImpl implements PerformanceCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Performance> getById(String id) {
        Performance performance = queryFactory.selectFrom(QPerformance.performance)
            .where(QPerformance.performance.id.eq(id))
            .fetchOne();
        return Optional.ofNullable(performance);
    }

}

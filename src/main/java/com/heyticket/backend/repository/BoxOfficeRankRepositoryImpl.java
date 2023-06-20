package com.heyticket.backend.repository;


import static com.heyticket.backend.domain.QBoxOfficeRank.boxOfficeRank;

import com.heyticket.backend.domain.BoxOfficeRank;
import com.heyticket.backend.domain.QBoxOfficeRank;
import com.heyticket.backend.module.kopis.enums.BoxOfficeArea;
import com.heyticket.backend.module.kopis.enums.BoxOfficeGenre;
import com.heyticket.backend.module.kopis.enums.TimePeriod;
import com.heyticket.backend.service.dto.request.BoxOfficeRankRequest;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
public class BoxOfficeRankRepositoryImpl implements BoxOfficeRankCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<BoxOfficeRank> findBoxOfficeRank(BoxOfficeRankRequest request) {
        BoxOfficeRank boxOfficeRank = queryFactory.selectFrom(QBoxOfficeRank.boxOfficeRank)
            .where(
                eqBoxOfficeGenre(request.getBoxOfficeGenre()),
                eqBoxOfficeArea(request.getBoxOfficeArea()),
                eqTimePeriod(request.getTimePeriod())
            )
            .orderBy(QBoxOfficeRank.boxOfficeRank.createdDate.desc())
            .limit(1)
            .fetchOne();
        return Optional.ofNullable(boxOfficeRank);
    }

    private BooleanExpression eqBoxOfficeGenre(BoxOfficeGenre boxOfficeGenre) {
        if (ObjectUtils.isEmpty(boxOfficeGenre) || boxOfficeGenre == BoxOfficeGenre.ALL) {
            return null;
        }
        return boxOfficeRank.boxOfficeGenre.eq(boxOfficeGenre);
    }

    private BooleanExpression eqBoxOfficeArea(BoxOfficeArea boxOfficeArea) {
        if (ObjectUtils.isEmpty(boxOfficeArea) || boxOfficeArea == BoxOfficeArea.ALL) {
            return null;
        }
        return boxOfficeRank.boxOfficeArea.eq(boxOfficeArea);
    }

    private BooleanExpression eqTimePeriod(TimePeriod timePeriod) {
        if (ObjectUtils.isEmpty(timePeriod)) {
            return null;
        }
        return boxOfficeRank.timePeriod.eq(timePeriod);
    }

}

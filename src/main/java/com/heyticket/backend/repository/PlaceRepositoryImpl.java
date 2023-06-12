package com.heyticket.backend.repository;


import static com.heyticket.backend.domain.QPlace.place;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findAllIds() {
        return queryFactory.select(place.id)
            .from(place)
            .fetch();
    }

}

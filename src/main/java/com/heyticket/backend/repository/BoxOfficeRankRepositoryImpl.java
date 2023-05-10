package com.heyticket.backend.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BoxOfficeRankRepositoryImpl implements BoxOfficeRankCustomRepository {

    private final JPAQueryFactory queryFactory;

}

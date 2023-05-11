package com.heyticket.backend.module.mapper;

import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.service.dto.BoxOfficeRankResponse;
import com.heyticket.backend.service.dto.PerformanceResponse;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-11T23:02:31+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.7 (Amazon.com Inc.)"
)
public class PerformanceMapperImpl implements PerformanceMapper {

    @Override
    public PerformanceResponse toPerformanceDto(Performance performance) {
        if ( performance == null ) {
            return null;
        }

        PerformanceResponse.PerformanceResponseBuilder performanceResponse = PerformanceResponse.builder();

        performanceResponse.id( performance.getId() );
        performanceResponse.placeId( performance.getPlaceId() );
        performanceResponse.title( performance.getTitle() );
        performanceResponse.startDate( performance.getStartDate() );
        performanceResponse.endDate( performance.getEndDate() );
        performanceResponse.place( performance.getPlace() );
        performanceResponse.cast( performance.getCast() );
        performanceResponse.crew( performance.getCrew() );
        performanceResponse.runtime( performance.getRuntime() );
        performanceResponse.age( performance.getAge() );
        performanceResponse.company( performance.getCompany() );
        performanceResponse.price( performance.getPrice() );
        performanceResponse.poster( performance.getPoster() );
        performanceResponse.story( performance.getStory() );
        performanceResponse.genre( performance.getGenre() );
        performanceResponse.state( performance.getState() );
        performanceResponse.openRun( performance.getOpenRun() );
        performanceResponse.dtguidance( performance.getDtguidance() );

        return performanceResponse.build();
    }

    @Override
    public BoxOfficeRankResponse toBoxOfficeRankResponse(Performance performance) {
        if ( performance == null ) {
            return null;
        }

        BoxOfficeRankResponse.BoxOfficeRankResponseBuilder boxOfficeRankResponse = BoxOfficeRankResponse.builder();

        boxOfficeRankResponse.id( performance.getId() );
        boxOfficeRankResponse.placeId( performance.getPlaceId() );
        boxOfficeRankResponse.title( performance.getTitle() );
        boxOfficeRankResponse.startDate( performance.getStartDate() );
        boxOfficeRankResponse.endDate( performance.getEndDate() );
        boxOfficeRankResponse.place( performance.getPlace() );
        boxOfficeRankResponse.cast( performance.getCast() );
        boxOfficeRankResponse.crew( performance.getCrew() );
        boxOfficeRankResponse.runtime( performance.getRuntime() );
        boxOfficeRankResponse.age( performance.getAge() );
        boxOfficeRankResponse.company( performance.getCompany() );
        boxOfficeRankResponse.price( performance.getPrice() );
        boxOfficeRankResponse.poster( performance.getPoster() );
        boxOfficeRankResponse.story( performance.getStory() );
        boxOfficeRankResponse.genre( performance.getGenre() );
        boxOfficeRankResponse.state( performance.getState() );
        boxOfficeRankResponse.openRun( performance.getOpenRun() );
        boxOfficeRankResponse.dtguidance( performance.getDtguidance() );

        return boxOfficeRankResponse.build();
    }
}

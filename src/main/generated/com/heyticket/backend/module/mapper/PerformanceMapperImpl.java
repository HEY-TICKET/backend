package com.heyticket.backend.module.mapper;

import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.service.dto.PerformanceDto;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-08T23:35:51+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.7 (Amazon.com Inc.)"
)
public class PerformanceMapperImpl implements PerformanceMapper {

    @Override
    public PerformanceDto toPerformanceDto(Performance performance) {
        if ( performance == null ) {
            return null;
        }

        PerformanceDto.PerformanceDtoBuilder performanceDto = PerformanceDto.builder();

        performanceDto.id( performance.getId() );
        performanceDto.placeId( performance.getPlaceId() );
        performanceDto.title( performance.getTitle() );
        performanceDto.startDate( performance.getStartDate() );
        performanceDto.endDate( performance.getEndDate() );
        performanceDto.place( performance.getPlace() );
        performanceDto.cast( performance.getCast() );
        performanceDto.crew( performance.getCrew() );
        performanceDto.runtime( performance.getRuntime() );
        performanceDto.age( performance.getAge() );
        performanceDto.company( performance.getCompany() );
        performanceDto.price( performance.getPrice() );
        performanceDto.poster( performance.getPoster() );
        performanceDto.story( performance.getStory() );
        performanceDto.genre( performance.getGenre() );
        performanceDto.state( performance.getState() );
        performanceDto.openRun( performance.getOpenRun() );
        performanceDto.dtguidance( performance.getDtguidance() );

        return performanceDto.build();
    }
}

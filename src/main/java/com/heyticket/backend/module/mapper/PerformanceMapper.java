package com.heyticket.backend.module.mapper;

import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.service.dto.response.BoxOfficeRankResponse;
import com.heyticket.backend.service.dto.response.PerformanceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PerformanceMapper {

    PerformanceMapper INSTANCE = Mappers.getMapper(PerformanceMapper.class);

    @Mapping(source = "performance.storyUrls", target = "storyUrls", ignore = true)
    @Mapping(target = "latitude", ignore = true)
    @Mapping(target = "longitude", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "sido", ignore = true)
    @Mapping(target = "gugun", ignore = true)
    @Mapping(source = "performance.place", target = "placeId", ignore = true)
    PerformanceResponse toPerformanceDto(Performance performance);

    @Mapping(source = "performance.storyUrls", target = "storyUrls", ignore = true)
    @Mapping(source = "performance.storyUrls", target = "rank", ignore = true)
    @Mapping(target = "placeId", ignore = true)
    BoxOfficeRankResponse toBoxOfficeRankResponse(Performance performance);

}

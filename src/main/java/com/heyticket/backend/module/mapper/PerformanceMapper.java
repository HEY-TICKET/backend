package com.heyticket.backend.module.mapper;

import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.service.dto.PerformanceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PerformanceMapper {

    PerformanceMapper INSTANCE = Mappers.getMapper(PerformanceMapper.class);

    @Mapping(source = "performance.storyUrls", target="storyUrls", ignore=true)
    PerformanceDto toPerformanceDto(Performance performance);

}

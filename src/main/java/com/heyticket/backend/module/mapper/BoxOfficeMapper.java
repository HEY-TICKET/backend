package com.heyticket.backend.module.mapper;

import com.heyticket.backend.domain.BoxOffice;
import com.heyticket.backend.service.dto.BoxOfficeDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BoxOfficeMapper {

    BoxOfficeMapper INSTANCE = Mappers.getMapper(BoxOfficeMapper.class);

    BoxOfficeDto toBoxOfficeDto(BoxOffice boxOffice);

}

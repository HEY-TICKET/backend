package com.heyticket.backend.module.mapper;

import com.heyticket.backend.domain.BoxOffice;
import com.heyticket.backend.service.dto.BoxOfficeDto;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-07T23:22:56+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.7 (Amazon.com Inc.)"
)
public class BoxOfficeMapperImpl implements BoxOfficeMapper {

    @Override
    public BoxOfficeDto toBoxOfficeDto(BoxOffice boxOffice) {
        if ( boxOffice == null ) {
            return null;
        }

        BoxOfficeDto.BoxOfficeDtoBuilder boxOfficeDto = BoxOfficeDto.builder();

        boxOfficeDto.id( boxOffice.getId() );
        boxOfficeDto.area( boxOffice.getArea() );
        boxOfficeDto.prfdtcnt( boxOffice.getPrfdtcnt() );
        boxOfficeDto.startDate( boxOffice.getStartDate() );
        boxOfficeDto.endDate( boxOffice.getEndDate() );
        boxOfficeDto.cate( boxOffice.getCate() );
        boxOfficeDto.prfplcnm( boxOffice.getPrfplcnm() );
        boxOfficeDto.prfnm( boxOffice.getPrfnm() );
        boxOfficeDto.rnum( boxOffice.getRnum() );
        boxOfficeDto.seatcnt( boxOffice.getSeatcnt() );
        boxOfficeDto.poster( boxOffice.getPoster() );
        boxOfficeDto.mt20id( boxOffice.getMt20id() );

        return boxOfficeDto.build();
    }
}

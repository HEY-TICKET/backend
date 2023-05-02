package com.heyticket.backend.performances.client;

import com.heyticket.backend.performances.domain.Performance;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record PerformanceResponse(
    String mt20id,
    String prfnm,
    String prfpdfrom,
    String prfpdto,
    String fcltynm,
    String poster,
    String genre,
    String prfstate,
    String openrun
) {

    public Performance toEntity() {
        return Performance.builder()
            .mt20id(mt20id())
            .title(prfnm())
            .startDate(LocalDate.parse(prfpdfrom(), DateTimeFormatter.ofPattern("yyyy.MM.dd")))
            .endDate(LocalDate.parse(prfpdto(), DateTimeFormatter.ofPattern("yyyy.MM.dd")))
            .place(fcltynm())
            .poster(poster())
            .genre(genre())
            .state(prfstate())
            .openrun(openrun().equals("Y"))
            .build();
    }
}

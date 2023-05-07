package com.heyticket.backend.service.dto;

import com.heyticket.backend.module.kopis.client.dto.KopisBoxOfficeRequest;
import com.heyticket.backend.module.kopis.enums.Area;
import com.heyticket.backend.module.kopis.enums.Genre;
import com.heyticket.backend.module.kopis.enums.TimePeriod;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoxOfficeRequest {

    private TimePeriod timePeriod;

    private LocalDate date;

    private Genre genre;

    private Area area;

    public KopisBoxOfficeRequest toKopisBoxOfficeRequest() {
        return KopisBoxOfficeRequest.builder()
            .ststype(this.timePeriod.getValue())
            .date(this.date.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .area(this.area.getValue())
            .build();
    }

}

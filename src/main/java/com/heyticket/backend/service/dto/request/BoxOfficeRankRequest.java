package com.heyticket.backend.service.dto.request;

import com.heyticket.backend.module.kopis.enums.BoxOfficeArea;
import com.heyticket.backend.module.kopis.enums.BoxOfficeGenre;
import com.heyticket.backend.service.enums.TimePeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoxOfficeRankRequest {

    private TimePeriod timePeriod;

    private BoxOfficeGenre boxOfficeGenre;

    private BoxOfficeArea boxOfficeArea;
}

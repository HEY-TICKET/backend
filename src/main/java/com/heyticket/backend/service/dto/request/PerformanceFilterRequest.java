package com.heyticket.backend.service.dto.request;

import com.heyticket.backend.domain.enums.PerformancePrice;
import com.heyticket.backend.domain.enums.PerformanceStatus;
import com.heyticket.backend.module.kopis.enums.Area;
import com.heyticket.backend.module.kopis.enums.Genre;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceFilterRequest {

    private Genre genre;

    private Area area;

    private LocalDate date;

    private PerformanceStatus state;

    private PerformancePrice price;
}

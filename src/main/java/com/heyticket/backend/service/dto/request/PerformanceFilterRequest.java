package com.heyticket.backend.service.dto.request;

import com.heyticket.backend.service.enums.PerformanceStatus;
import com.heyticket.backend.service.enums.Area;
import com.heyticket.backend.service.enums.Genre;
import com.heyticket.backend.service.enums.SortOrder;
import com.heyticket.backend.service.enums.SortType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceFilterRequest {

    @Builder.Default
    private List<Genre> genres = new ArrayList<>();

    @Builder.Default
    private List<Area> areas = new ArrayList<>();

    private LocalDate date;

    @Builder.Default
    private List<PerformanceStatus> statuses = new ArrayList<>();

    private Integer minPrice;

    private Integer maxPrice;

    @Builder.Default
    private SortType sortType = SortType.VIEWS;

    @Builder.Default
    private SortOrder sortOrder = SortOrder.DESC;
}

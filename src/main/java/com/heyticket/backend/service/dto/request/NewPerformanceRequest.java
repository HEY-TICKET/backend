package com.heyticket.backend.service.dto.request;

import com.heyticket.backend.service.enums.Genre;
import com.heyticket.backend.service.enums.SortOrder;
import com.heyticket.backend.service.enums.SortType;
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
public class NewPerformanceRequest {

    private Genre genre;

    private SortType sortType;

    private SortOrder sortOrder;
}

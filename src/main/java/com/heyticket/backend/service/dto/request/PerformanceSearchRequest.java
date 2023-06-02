package com.heyticket.backend.service.dto.request;

import com.heyticket.backend.service.enums.SearchType;
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
public class PerformanceSearchRequest {

    private SearchType searchType;

    private String query;
}

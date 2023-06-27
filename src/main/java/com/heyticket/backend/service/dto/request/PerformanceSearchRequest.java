package com.heyticket.backend.service.dto.request;

import com.heyticket.backend.service.enums.SearchType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceSearchRequest {

    @NotBlank
    private SearchType searchType;

    @NotBlank
    private String query;
}

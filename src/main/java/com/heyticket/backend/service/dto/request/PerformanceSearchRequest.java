package com.heyticket.backend.service.dto.request;

import com.heyticket.backend.service.enums.SearchType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private SearchType searchType;

    @NotBlank
    private String query;
}

package com.heyticket.backend.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceBatchUpdateRequest {

    @NotBlank
    private LocalDate from;

    @NotBlank
    private LocalDate to;

    @NotBlank
    private int rows;
}

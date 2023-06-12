package com.heyticket.backend.service.dto.request;

import java.time.LocalDate;
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
public class PerformanceBatchUpdateRequest {

    private LocalDate from;

    private LocalDate to;

    private int rows;
}

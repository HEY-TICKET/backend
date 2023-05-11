package com.heyticket.backend.service.dto;

import com.heyticket.backend.module.kopis.enums.Genre;
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

}

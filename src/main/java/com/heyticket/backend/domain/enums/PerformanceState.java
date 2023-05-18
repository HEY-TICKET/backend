package com.heyticket.backend.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PerformanceState {

    UPCOMING("공연예정"),
    ONGOING("공연중"),
    COMPLETED("공연종료");

    private final String name;

}

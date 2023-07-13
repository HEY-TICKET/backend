package com.heyticket.backend.service.enums;

import java.util.Arrays;
import java.util.NoSuchElementException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PerformanceStatus {

    UPCOMING("공연예정"),
    ONGOING("공연중"),
    COMPLETED("공연완료");

    private final String name;

    public static PerformanceStatus getByName(String name) {
        return Arrays.stream(PerformanceStatus.values())
            .filter(performanceStatus -> performanceStatus.getName().equals(name))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("No such status."));
    }
}

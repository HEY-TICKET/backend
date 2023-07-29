package com.heyticket.backend.service.enums;

import java.util.Arrays;
import java.util.NoSuchElementException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PerformanceStatus {

    ONGOING("공연중", 1),
    UPCOMING("공연예정", 2),
    COMPLETED("공연완료", 3);

    private final String name;

    private final int priority;

    public static PerformanceStatus getByName(String name) {
        return Arrays.stream(PerformanceStatus.values())
            .filter(performanceStatus -> performanceStatus.getName().equals(name))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("No such status."));
    }

    public static PerformanceStatus getByPriority(String priority) {
        return Arrays.stream(PerformanceStatus.values())
            .filter(performanceStatus -> performanceStatus.getPriority() == Integer.parseInt(priority))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("No such status."));
    }
}

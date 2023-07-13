package com.heyticket.backend.service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TimePeriod {

    DAY("day"),
    WEEK("week"),
    MONTH("month");

    private final String value;
}

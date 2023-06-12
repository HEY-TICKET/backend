package com.heyticket.backend.module.kopis.enums;

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

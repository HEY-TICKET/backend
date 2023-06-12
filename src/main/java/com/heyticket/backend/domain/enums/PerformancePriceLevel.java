package com.heyticket.backend.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PerformancePriceLevel {

    PRICE1(0, 10000),
    PRICE2(10000, 40000),
    PRICE3(40000, 70000),
    PRICE4(70000, 100000),
    PRICE5(100000, 100000000);

    private final int lowPrice;

    private final int highPrice;
}

package com.heyticket.backend.module.kopis.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoxOfficeArea {

    SEOUL("11"),
    INCHEON("28"),
    DAEJEON("30"),
    DAEGU("27"),
    GWANGJU("29"),
    BUSAN("26"),
    ULSAN("31"),
    SEJONG("36"),
    GYEONGGI("41"),
    CHUNGCHEONG("43|44"),
    GYEONGSANG("47|48"),
    JEOLLA("45|46"),
    GANGWON("42"),
    JEJU("50"),
    UNI("UNI");

    private final String value;

}
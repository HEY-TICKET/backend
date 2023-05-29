package com.heyticket.backend.module.kopis.enums;

import java.util.Arrays;
import java.util.NoSuchElementException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoxOfficeArea {

    SEOUL("11", "서울"),
    BUSAN("26", "부산"),
    DAEGU("27", "대구"),
    INCHEON("28", "인천"),
    GWANGJU("29", "광주"),
    DAEJEON("30", "대전"),
    ULSAN("31", "울산"),
    SEJONG("36", "세종"),
    GYEONGGI("41", "경기"),
    GANGWON("42", "강원"),
    // Merged in BoxOffice
    CHUNGCHEONG("43|44", "충청"),
    JEOLLA("45|46", "전라"),
    GYEONGSANG("47|48", "경상"),
    JEJU("50", "제주"),
    // BoxOffice only
    UNI("UNI", "대학로"),
    ALL("NONE", "전체");

    private final String code;

    private final String name;

    public static BoxOfficeArea getByName(String name) {
        return Arrays.stream(BoxOfficeArea.values())
            .filter(boxOfficeArea -> boxOfficeArea.getName().equals(name))
            .findFirst().orElseThrow(() -> new NoSuchElementException("no such boxOfficeArea. name : " + name));
    }
}
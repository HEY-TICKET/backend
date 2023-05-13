package com.heyticket.backend.module.kopis.enums;

import java.util.Arrays;
import java.util.NoSuchElementException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoxOfficeArea {

    SEOUL("11", "서울"),
    INCHEON("28", "인천"),
    DAEJEON("30", "대전"),
    DAEGU("27", "대구"),
    GWANGJU("29", "광주"),
    BUSAN("26", "부산"),
    ULSAN("31", "울산"),
    SEJONG("36", "세종"),
    GYEONGGI("41", "경기"),
    CHUNGCHEONG("43|44", "충청"),
    GYEONGSANG("47|48", "경상"),
    JEOLLA("45|46", "전라"),
    GANGWON("42", "강원"),
    JEJU("50", "제주"),
    UNI("UNI", "대학로");

    private final String code;

    private final String name;

    public static BoxOfficeArea getByName(String name) {


        return Arrays.stream(BoxOfficeArea.values())
            .filter(boxOfficeArea -> boxOfficeArea.getName().equals(name))
            .findFirst().orElseThrow(() -> new NoSuchElementException("no such boxOfficeArea. name : " + name));
    }

}
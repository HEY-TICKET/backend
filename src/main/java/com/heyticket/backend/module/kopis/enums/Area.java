package com.heyticket.backend.module.kopis.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Area {

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
    CHUNGBUK("43", "충청"),
    CHUNGNAM("44", "충청"),
    JEONBUK("45", "전라"),
    JEONNAM("46", "전라"),
    GYEONGBUK("47", "경상"),
    GYEONGNAM("48", "경상"),
    JEJU("50", "제주");

    private final String code;

    private final String name;

    public static Area getByName(String name) {
        return Arrays.stream(Area.values())
            .filter(boxOfficeArea -> boxOfficeArea.getName().equals(name))
            .findFirst().orElseThrow(() -> new NoSuchElementException("no such boxOfficeArea. name : " + name));
    }

    public static List<Area> getByNames(List<String> names) {
        List<Area> areas = new ArrayList<>();
        for (String name : names) {
            Area[] values = Area.values();
            Area area = Arrays.stream(values)
                .filter(value -> value.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No such area name"));
            areas.add(area);
        }
        return areas;
    }
}
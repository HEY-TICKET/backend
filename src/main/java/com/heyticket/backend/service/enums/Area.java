package com.heyticket.backend.service.enums;

import com.heyticket.backend.module.kopis.enums.BoxOfficeArea;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Area {

    SEOUL("11", "서울", BoxOfficeArea.SEOUL),
    BUSAN("26", "부산", BoxOfficeArea.BUSAN),
    DAEGU("27", "대구", BoxOfficeArea.DAEGU),
    INCHEON("28", "인천", BoxOfficeArea.INCHEON),
    GWANGJU("29", "광주", BoxOfficeArea.GWANGJU),
    DAEJEON("30", "대전", BoxOfficeArea.DAEJEON),
    ULSAN("31", "울산", BoxOfficeArea.ULSAN),
    SEJONG("36", "세종", BoxOfficeArea.SEJONG),
    GYEONGGI("41", "경기", BoxOfficeArea.GYEONGGI),
    GANGWON("42", "강원", BoxOfficeArea.GYEONGSANG),

    CHUNGBUK("43", "충북", BoxOfficeArea.CHUNGCHEONG),
    CHUNGNAM("44", "충남", BoxOfficeArea.CHUNGCHEONG),
    JEONBUK("45", "전북", BoxOfficeArea.JEOLLA),
    JEONNAM("46", "전남", BoxOfficeArea.JEOLLA),
    GYEONGBUK("47", "경북", BoxOfficeArea.GYEONGSANG),
    GYEONGNAM("48", "경남", BoxOfficeArea.GYEONGSANG),
    JEJU("50", "제주", BoxOfficeArea.JEJU);

    private final String code;

    private final String name;

    private final BoxOfficeArea boxOfficeArea;

    public static Area getByName(String name) {
        return Arrays.stream(Area.values())
            .filter(boxOfficeArea -> boxOfficeArea.getName().equals(name))
            .findFirst().orElseThrow(() -> new NoSuchElementException("no such area. name : " + name));
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
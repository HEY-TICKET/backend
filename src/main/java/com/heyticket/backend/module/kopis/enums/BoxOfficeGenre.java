package com.heyticket.backend.module.kopis.enums;

import java.util.Arrays;
import java.util.NoSuchElementException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoxOfficeGenre {

    THEATER("AAAA", "연극"),
    MUSICAL("GGGA", "뮤지컬"),
    CLASSIC("CCCA", "클래식"),
    KOREAN_TRADITIONAL_MUSIC("CCCC", "국악"),
    POPULAR_MUSIC("CCCD", "대중음악"),
    DANCE("BBBC", "무용"),
    POPULAR_DANCE("BBBR", "대중무용"),
    CIRCUS_AND_MAGIC("EEEB", "서커스/마술"),
    MIXED_GENRE("EEEA", "복합"),
    ALL("NONE", "전체"),
    // BoxOffice only,
    KID("KID", "아동"),
    OPEN("OPEN", "오픈런");

    private final String code;

    private final String name;

    public static BoxOfficeGenre getByName(String name) {
        return Arrays.stream(BoxOfficeGenre.values())
            .filter(boxOfficeGenre -> boxOfficeGenre.getName().equals(name))
            .findFirst().orElseThrow(() -> new NoSuchElementException("No such genre name. name : " + name));
    }
}

package com.heyticket.backend.module.kopis.enums;

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
    CONTEMPORARY_DANCE("BBBR", "대중무용"),
    CIRCUS_AND_MAGIC("EEEB", "서커스/마술"),
    MIXED_GENRE("EEEA", "복합"),
    KID("KID", "아동"),
    OPEN("OPEN", "오픈런");

    private final String code;

    private final String name;

}

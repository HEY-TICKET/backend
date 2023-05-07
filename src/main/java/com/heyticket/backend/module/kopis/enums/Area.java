package com.heyticket.backend.module.kopis.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Area {

    SEOUL("AAAA", "연극", 1),
    DAEJEON("BBBC", "무용", 2),
    CONTEMPORARY_DANCE("BBBE", "대중무용", 3),
    CLASSIC("CCCA", "클래식", 4),
    KOREAN_TRADITIONAL_MUSIC("CCCC", "국악", 5),
    POPULAR_MUSIC("CCCD", "대중음악", 6),
    MIXED_GENRE("EEEA", "복합", 7),
    CIRCUS_AND_MAGIC("EEEB", "서커스/마술", 8),
    MUSICAL("GGGA", "뮤지컬", 9);

    private final String code;

    private final String name;

    private final int number;

}

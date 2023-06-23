package com.heyticket.backend.service.enums;

import com.heyticket.backend.module.kopis.enums.BoxOfficeGenre;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Genre {

    THEATER("AAAA", "연극", BoxOfficeGenre.THEATER),
    MUSICAL("GGGA", "뮤지컬", BoxOfficeGenre.MUSICAL),
    CLASSIC("CCCA", "서양음악(클래식)", BoxOfficeGenre.CLASSIC),
    KOREAN_TRADITIONAL_MUSIC("CCCC", "한국음악(국악)", BoxOfficeGenre.KOREAN_TRADITIONAL_MUSIC),
    POPULAR_MUSIC("CCCD", "대중음악", BoxOfficeGenre.POPULAR_MUSIC),
    DANCE("BBBC", "무용", BoxOfficeGenre.DANCE),
    POPULAR_DANCE("BBBE", "대중무용", BoxOfficeGenre.POPULAR_DANCE),
    CIRCUS_AND_MAGIC("EEEB", "서커스/마술", BoxOfficeGenre.CIRCUS_AND_MAGIC),
    MIXED_GENRE("EEEA", "복합", BoxOfficeGenre.MIXED_GENRE),
    ALL("ALL", "전체", null);

    private final String code;

    private final String name;

    private final BoxOfficeGenre boxOfficeGenre;

    public static Genre getByName(String name) {
        Genre[] values = Genre.values();
        return Arrays.stream(values)
            .filter(value -> value.getName().equals(name))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("No such genre name"));
    }

    public static List<Genre> getByNames(List<String> names) {
        List<Genre> genres = new ArrayList<>();
        for (String name : names) {
            Genre[] values = Genre.values();
            Genre genre = Arrays.stream(values)
                .filter(value -> value.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No such genre name"));
            genres.add(genre);
        }
        return genres;
    }
}

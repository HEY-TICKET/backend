package com.heyticket.backend.service.dto.response;

import com.heyticket.backend.module.kopis.enums.Area;
import com.heyticket.backend.module.kopis.enums.Genre;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {

    private String email;

    private boolean allowKeywordPush;

    private boolean allowMarketing;

    @Builder.Default
    private List<Genre> genres = new ArrayList<>();

    @Builder.Default
    private List<Area> areas = new ArrayList<>();

    @Builder.Default
    private List<String> keywords = new ArrayList<>();
}


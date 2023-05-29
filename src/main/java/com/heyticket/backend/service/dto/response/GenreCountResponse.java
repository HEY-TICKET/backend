package com.heyticket.backend.service.dto.response;

import com.heyticket.backend.module.kopis.enums.Genre;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenreCountResponse {

    private Genre genre;

    private Long count;
}

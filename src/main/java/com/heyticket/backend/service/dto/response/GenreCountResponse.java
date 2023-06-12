package com.heyticket.backend.service.dto.response;

import com.heyticket.backend.module.kopis.enums.Genre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenreCountResponse {

    private Genre genre;

    private Long count;
}

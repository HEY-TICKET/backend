package com.heyticket.backend.service.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenreCountResponse {

    private String genre;

    private Long count;
}

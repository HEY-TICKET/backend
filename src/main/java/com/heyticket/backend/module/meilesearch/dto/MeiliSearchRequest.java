package com.heyticket.backend.module.meilesearch.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeiliSearchRequest {

    private String q;

    private Integer page;

    private Integer hitsPerPage;

    private List<String> attributesToHighlight;

    private List<String> sort;
}

package com.heyticket.backend.module.meilesearch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeiliSearchResponse {

    private String query;
    private int processingTimeMs;
    private int hitsPerPage;
    private int page;
    private int totalPages;
    private int totalHits;
    @JsonProperty("hits")
    private List<MeiliPerformanceResponse> performanceResponses;
}

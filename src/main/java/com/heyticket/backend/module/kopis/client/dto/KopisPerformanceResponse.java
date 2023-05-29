package com.heyticket.backend.module.kopis.client.dto;

public record KopisPerformanceResponse(
    String mt20id,
    String prfnm,
    String prfpdfrom,
    String prfpdto,
    String fcltynm,
    String poster,
    String genre,
    String prfstate,
    String openrun
) {

}

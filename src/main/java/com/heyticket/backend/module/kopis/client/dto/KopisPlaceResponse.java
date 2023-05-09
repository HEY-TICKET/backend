package com.heyticket.backend.module.kopis.client.dto;

public record KopisPlaceResponse(
    String fcltynm, // 	공연시설명	올림픽공원
    String mt10id, // 	공연시설ID	FC001247
    Integer mt13cnt, // 	공연장 수	9
    String fcltychartr, // 	시설특성	기타(공공)
    String sidonm, // 지역(시도)
    String gugunnm, // 지역(구군)
    String opende // 	개관연도	1986
) {

}

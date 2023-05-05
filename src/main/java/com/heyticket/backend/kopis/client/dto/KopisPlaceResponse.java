package com.heyticket.backend.kopis.client.dto;

public record KopisPlaceResponse(
    String fcltynm, // 	공연시설명	올림픽공원
    String mt10id, // 	공연시설ID	FC001247
    Integer mt13cnt, // 	공연장 수	9
    String fcltychartr, // 	시설특성	기타(공공)
    Integer opende, // 	개관연도	1986
    Integer seatscale, // 	객석 수	32349
    String telno, // 	전화번호	02-410-1114
    String relateurl, // 	홈페이지	http://www.olympicpark.co.kr/
    String adres, // 	주소	서울특별시 송파구 방이동
    Double la, // 	위도	37.52112
    Double lo // 	경도	127.12836360000005
) {

}

package com.heyticket.backend.kopis.client.dto;

public record BoxOfficeResponse(
    String area, // 지역
    Integer prfdtcnt, // 상영 횟수
    String prfpd, // 공연 기간
    String cate, // 장르
    String prfplcnm, // 공연장
    String prfnm, // 순위
    Integer rnum, // 좌석수
    String seatcnt,
    String poster,
    String mt20id
) {

}

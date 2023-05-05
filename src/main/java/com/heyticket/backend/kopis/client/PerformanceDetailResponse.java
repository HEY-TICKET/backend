package com.heyticket.backend.kopis.client;

public record PerformanceDetailResponse(
    String mt20id, // 공연 ID
    String mt10id, // 공연시설 ID
    String prfnm, // 공연명
    String prfpdfrom, // 공연시 작일
    String prfpdto, // 공연 종료일
    String fcltynm, // 공연 시설명
    String prfcast, // 공연 출연진
    String prfcrew, // 공연 제작진
    String prfruntime, // 공연
    String prfage, // 공연
    String entrpsnm, // 제작사
    String pcseguidance, // 티켓 가격
    String poster, // 포스터 이미지경로
    String sty, // 줄거리
    String genrenm, // 장르
    String prfstate, // 공연 상태
    String openrun, // 오픈런
    String[] styurls, // 소개이미지 목록
    String dtguidance // 공연 시간
) {

}

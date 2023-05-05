package com.heyticket.backend.kopis.client;

import com.heyticket.backend.kopis.domain.Performance;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record PerformanceDetailResponse(
    String mt20id, // 공연 ID
    String mt10id, // 공연시설 ID
    String prfnm, // 공연명
    String prfpdfrom, // 공연 시작일
    String prfpdto, // 공연 종료일
    String fcltynm, // 공연 시설명
    String prfcast, // 공연 출연진
    String prfcrew, // 공연 제작진
    String prfruntime, // 공연 런타임
    String prfage, // 공연 관람 연령
    String entrpsnm, // 제작사
    String pcseguidance, // 티켓 가격
    String poster, // 포스터 이미지경로
    String sty, // 줄거리
    String genrenm, // 장르
    String prfstate, // 공연 상태
    String openrun, // 오픈런
    String styurls, // 소개이미지 목록
    String dtguidance // 공연 시간
) {

    public Performance toEntity() {
        return Performance.builder()
            .id(this.mt20id)
            .placeId(this.mt10id)
            .title(this.prfnm)
            .startDate(LocalDate.parse(this.prfpdfrom, DateTimeFormatter.ofPattern("yyyy.MM.dd")))
            .endDate(LocalDate.parse(this.prfpdto, DateTimeFormatter.ofPattern("yyyy.MM.dd")))
            .place(this.fcltynm)
            .cast(this.prfcast)
            .crew(this.prfcrew)
            .runtime(this.prfruntime)
            .age(this.prfage)
            .company(this.entrpsnm)
            .price(this.pcseguidance)
            .poster(this.poster)
            .story(this.sty)
            .genre(this.genrenm)
            .state(this.prfstate)
            .openRun(this.openrun.equals("Y"))
            .build();
    }

}

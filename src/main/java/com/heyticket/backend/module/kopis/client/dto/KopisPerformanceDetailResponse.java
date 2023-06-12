package com.heyticket.backend.module.kopis.client.dto;

import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.domain.enums.PerformanceStatus;
import com.heyticket.backend.module.kopis.enums.Genre;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record KopisPerformanceDetailResponse(
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
    String[] styurls, // 소개이미지 목록
    String dtguidance // 공연 시간
) {

    public Performance toEntity() {

        return Performance.builder()
            .id(this.mt20id)
            .title(this.prfnm)
            .startDate(LocalDate.parse(this.prfpdfrom, DateTimeFormatter.ofPattern("yyyy.MM.dd")))
            .endDate(LocalDate.parse(this.prfpdto, DateTimeFormatter.ofPattern("yyyy.MM.dd")))
            .theater(this.fcltynm)
            .cast(this.prfcast.equals(" ") ? null : this.prfcast)
            .crew(this.prfcrew.equals(" ") ? null : this.prfcrew)
            .runtime(this.prfruntime.equals(" ") ? null : this.prfruntime)
            .age(this.prfage.equals(" ") ? null : this.prfage)
            .company(this.entrpsnm.equals(" ") ? null : this.entrpsnm)
            .price(this.pcseguidance.equals(" ") ? null : this.pcseguidance)
            .poster(this.poster.equals(" ") ? null : this.poster)
            .story(this.sty.equals(" ") ? null : this.sty)
            .schedule(this.dtguidance.equals(" ") ? null : this.dtguidance)
            .genre(Genre.getByName(this.genrenm))
            .status(PerformanceStatus.getByName(this.prfstate))
            .openRun(this.openrun.equals("Y"))
            .storyUrls(
                this.styurls != null ?
                    String.join("|", this.styurls) : null
            )
            .views(0)
            .build();
    }

}

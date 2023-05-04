package com.heyticket.backend.kopis.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Performance extends BaseTimeEntity {

    @Id
    private String id; // 공연 ID
    private String mt10id; // 공연시설 ID
    private String title; // 공연명
    private LocalDate startDate; // 공연시작일
    private LocalDate endDate; // 공연종료일
    private String place; // 공연시설명(공연장명)
    private String cast; // 출연진
    private String crew; // 제작진
    private String runtime; // 공연시간
    private String age; // 관람연령
    private String entrpsnm; // 제작사
    private String pcseguidance; // 티켓가격
    private String poster; // 포스터 이미지 경로
    @Column(columnDefinition = "TEXT")
    private String sty; // 줄거리
    private String genre; // 장르
    private String state; // 공연상태
    private Boolean openrun; // 오픈런 여부
    private String dtguidance; // 공연시간

}

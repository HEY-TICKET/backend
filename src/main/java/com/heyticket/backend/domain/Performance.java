package com.heyticket.backend.domain;

import com.heyticket.backend.service.enums.PerformanceStatus;
import com.heyticket.backend.service.enums.Area;
import com.heyticket.backend.service.enums.Genre;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Performance extends BaseTimeEntity {

    @Id
    private String id; // 공연 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;
    private String title; // 공연명
    private LocalDate startDate; // 공연 시작일
    private LocalDate endDate; // 공연 종료일
    private String theater; // 공연 시설명(공연장명)
    private String cast; // 출연진
    private String crew; // 제작진
    private String runtime; // 공연 런타임
    private String age; // 관람 연령
    private String company; // 제작사
    private String price; // 티켓 가격
    private String poster; // 포스터 이미지 경로
    @Column(columnDefinition = "TEXT")
    private String story; // 줄거리
    @Enumerated(EnumType.STRING)
    private Genre genre; // 장르
    @Enumerated(EnumType.STRING)
    private PerformanceStatus status; // 공연상태
    private Boolean openRun; // 오픈런 여부
    @Enumerated(EnumType.STRING)
    private Area area;
    @Column(length = 700)
    private String storyUrls; // 소개이미지 목록
    private String schedule; // 공연 시간
    @ColumnDefault(value = "0")
    private Integer views;

    public void addViewCount() {
        views++;
    }

    public void updateStatus(PerformanceStatus status) {
        this.status = status;
    }

    public void updateArea(Area area) {
        this.area = area;
    }

    public void updatePlace(Place place) {
        this.place = place;
    }
}

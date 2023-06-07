package com.heyticket.backend.service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.heyticket.backend.domain.enums.PerformanceStatus;
import com.heyticket.backend.module.kopis.enums.Genre;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PerformanceResponse {

    private String id; // 공연 ID
    private String placeId; // 공연 시설 ID
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
    private String story; // 줄거리
    private Genre genre; // 장르
    private PerformanceStatus status; // 공연상태
    private Boolean openRun; // 오픈런 여부
    private List<String> storyUrls; // 소개이미지 목록
    private String schedule; // 공연 시간
    private Integer views;
    private Double latitude;    // 위도
    private Double longitude;   // 경도
    private String address; // 주소
    private String phoneNumber; // 전화 번호
    private String sido; // 시도
    private String gugun; // 구군

    public void updateStoryUrls(String storyUrls) {
        if (StringUtils.hasText(storyUrls)) {
            this.storyUrls = List.of(storyUrls.split("\\|"));
        } else {
            this.storyUrls = Collections.emptyList();
        }
    }

    public void updateLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

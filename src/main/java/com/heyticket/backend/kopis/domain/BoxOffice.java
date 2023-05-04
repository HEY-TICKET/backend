package com.heyticket.backend.kopis.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
public class BoxOffice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String area; // 지역
    private int prfdtcnt; // 상연횟수
    private LocalDate startDate; // 공연 시작 날짜
    private LocalDate endDate; // 공연 종료 날짜
    private String cate; // 장르
    private String prfplcnm; // 공연장
    private String prfnm; // 공연명
    private String rnum; // 순위
    private String seatcnt; // 좌석수
    private String poster; // 포스터이미지
    private String mt20id; // 공연ID

}

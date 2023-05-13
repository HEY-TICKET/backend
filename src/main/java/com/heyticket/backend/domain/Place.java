package com.heyticket.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Place extends BaseTimeEntity {

    @Id
    private String id;          // 공연시설ID
    private String name;        // 공연시설명
    private int stageCount;      // 공연장 수
    private String Characteristic;   // 시설특성
    private String sidoName;    // 지역(시도)
    private String gugunName;   // 지역(구군)
    private String openYear;    // 개관연도
    private int seatScale;  // 객석 수
    private String phoneNumber; // 전화번호
    private String relateUrl;   // 홈페이지
    private String address;     // 주소
    private double latitude;    // 위도
    private double longitude;   // 경도

    public void updateSidoGugun(String sidoName, String gugunName) {
        this.sidoName = sidoName;
        this.gugunName = gugunName;
    }

}

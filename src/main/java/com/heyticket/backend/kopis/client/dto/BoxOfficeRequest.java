package com.heyticket.backend.kopis.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class BoxOfficeRequest {

    private String service; // 인증키

    private String ststype; // 요청 형태

    private String date; // 날짜

    private String catecode; // 장르 구분 코드, optional

    private String area; // 지역 코드, optional

    public void updateApiKey(String apiKey) {
        this.service = apiKey;
    }

}

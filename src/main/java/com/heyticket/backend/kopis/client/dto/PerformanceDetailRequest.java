package com.heyticket.backend.kopis.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class PerformanceDetailRequest {

    private String service;

    private String mt20id;

    public void updateApiKey(String apiKey) {
        this.service = apiKey;
    }

}

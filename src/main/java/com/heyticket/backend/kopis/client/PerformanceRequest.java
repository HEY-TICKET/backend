package com.heyticket.backend.kopis.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class PerformanceRequest {

    private String service;

    private String stdate;

    private String eddate;

    private int cpage;

    private int rows;

    public void updateApiKey(String apiKey) {
        this.service = apiKey;
    }

}

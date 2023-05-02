package com.heyticket.backend.performances.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class KopisPerformanceRequest {

    private String service;

    private String stdate;

    private String eddate;

    private int cpage;

    private int rows;

}

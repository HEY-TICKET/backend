package com.heyticket.backend.service.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PerformancePushInfo implements PushInfo {

    private String performanceId;

    private String performanceTitle;

    public static PerformancePushInfo of(String performanceId, String title) {
        return new PerformancePushInfo(performanceId, title);
    }

    @Override
    public String getTitle() {
        return "공연 소식";
    }

    @Override
    public String getBody() {
        return performanceTitle;
    }

    @Override
    public String getId() {
        return performanceId;
    }

    @Override
    public String getType() {
        return PushInfoType.PERFORMANCE.name();
    }
}

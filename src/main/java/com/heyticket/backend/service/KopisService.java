package com.heyticket.backend.service;

import com.heyticket.backend.kopis.client.BoxOfficeRequest;
import com.heyticket.backend.kopis.client.KopisFeignClient;
import com.heyticket.backend.kopis.client.PerformanceDetailResponse;
import com.heyticket.backend.kopis.client.PerformanceRequest;
import com.heyticket.backend.kopis.client.PerformanceResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KopisService {

    @Value("${kopis.api-key}")
    private String apiKey;

    private final KopisFeignClient kopisFeignClient;

    public List<PerformanceResponse> getPerformance(PerformanceRequest performanceRequest) {
        performanceRequest.updateApiKey(apiKey);
        return kopisFeignClient.getPerformances(performanceRequest);
    }

    public List<PerformanceDetailResponse> getPerformanceDetail(String performanceId) {
        return kopisFeignClient.getPerformanceDetail(performanceId, apiKey);
    }

    public List<BoxOfficeRequest> getBoxOffice(BoxOfficeRequest boxOfficeRequest) {
        boxOfficeRequest.updateApiKey(apiKey);
        return kopisFeignClient.getBoxOffice(boxOfficeRequest);
    }

}

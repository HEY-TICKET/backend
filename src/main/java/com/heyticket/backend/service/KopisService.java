package com.heyticket.backend.service;

import com.heyticket.backend.kopis.client.dto.BoxOfficeRequest;
import com.heyticket.backend.kopis.client.KopisFeignClient;
import com.heyticket.backend.kopis.client.dto.PerformanceDetailResponse;
import com.heyticket.backend.kopis.client.dto.PerformanceRequest;
import com.heyticket.backend.kopis.client.dto.PerformanceResponse;
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

    public List<PerformanceResponse> getPerformances(PerformanceRequest performanceRequest) {
        performanceRequest.updateApiKey(apiKey);
        return kopisFeignClient.getPerformances(performanceRequest);
    }

    public PerformanceDetailResponse getPerformanceDetail(String performanceId) {
        PerformanceDetailResponse performanceDetailResponse = kopisFeignClient.getPerformanceDetail(performanceId, apiKey).get(0);
        if (performanceDetailResponse.mt20id() == null) {
            throw new IllegalStateException("Fail to get performance detail.");
        }
        return performanceDetailResponse;
    }

    public List<BoxOfficeRequest> getBoxOffice(BoxOfficeRequest boxOfficeRequest) {
        boxOfficeRequest.updateApiKey(apiKey);
        return kopisFeignClient.getBoxOffice(boxOfficeRequest);
    }

}

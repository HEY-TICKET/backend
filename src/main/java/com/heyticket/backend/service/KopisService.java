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

    public List<PerformanceResponse> getPerformances(PerformanceRequest performanceRequest) {
        performanceRequest.updateApiKey(apiKey);
        return kopisFeignClient.getPerformances(performanceRequest);
    }

    public PerformanceDetailResponse getPerformanceDetail(String performanceId) {
        PerformanceDetailResponse performanceDetail = kopisFeignClient.getPerformanceDetail(performanceId, apiKey);
        if (performanceDetail.mt20id() == null) {
            throw new IllegalStateException("Fail to get performance detail.");
        }
        return performanceDetail;
    }

    public List<BoxOfficeRequest> getBoxOffice(BoxOfficeRequest boxOfficeRequest) {
        boxOfficeRequest.updateApiKey(apiKey);
        return kopisFeignClient.getBoxOffice(boxOfficeRequest);
    }

}

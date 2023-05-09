package com.heyticket.backend.service;

import com.heyticket.backend.module.kopis.client.KopisFeignClient;
import com.heyticket.backend.module.kopis.client.dto.KopisBoxOfficeRequest;
import com.heyticket.backend.module.kopis.client.dto.KopisBoxOfficeResponse;
import com.heyticket.backend.module.kopis.client.dto.KopisPerformanceDetailResponse;
import com.heyticket.backend.module.kopis.client.dto.KopisPerformanceRequest;
import com.heyticket.backend.module.kopis.client.dto.KopisPerformanceResponse;
import com.heyticket.backend.module.kopis.client.dto.KopisPlaceDetailResponse;
import com.heyticket.backend.module.kopis.client.dto.KopisPlaceRequest;
import com.heyticket.backend.module.kopis.client.dto.KopisPlaceResponse;
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

    public List<KopisPerformanceResponse> getPerformances(KopisPerformanceRequest kopisPerformanceRequest) {
        return kopisFeignClient.getPerformances(kopisPerformanceRequest, apiKey);
    }

    public KopisPerformanceDetailResponse getPerformanceDetail(String performanceId) {
        KopisPerformanceDetailResponse kopisPerformanceDetailResponse = kopisFeignClient.getPerformanceDetail(performanceId, apiKey).get(0);
        if (kopisPerformanceDetailResponse.mt20id() == null) {
            throw new IllegalStateException("Fail to get performance detail.");
        }
        return kopisPerformanceDetailResponse;
    }

    public List<KopisBoxOfficeResponse> getBoxOffice(KopisBoxOfficeRequest kopisBoxOfficeRequest) {
        return kopisFeignClient.getBoxOffice(kopisBoxOfficeRequest, apiKey);
    }

    public List<KopisPlaceResponse> getPlaces(KopisPlaceRequest kopisPlaceRequest) {
        return kopisFeignClient.getPlaces(kopisPlaceRequest, apiKey);
    }

    public KopisPlaceDetailResponse getPlaceDetail(String placeId) {
        return kopisFeignClient.getPlaceDetail(placeId, apiKey).get(0);
    }

}

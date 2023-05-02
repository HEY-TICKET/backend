package com.heyticket.backend.service;

import com.heyticket.backend.performances.client.KopisFeignClient;
import com.heyticket.backend.performances.client.KopisPerformanceRequest;
import com.heyticket.backend.performances.client.PerformanceResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    public List<PerformanceResponse> getPerformanceFromKopis() {
        KopisPerformanceRequest performanceRequest = KopisPerformanceRequest.builder()
            .service(apiKey)
            .stdate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .eddate(LocalDate.now().plusMonths(3).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .cpage(1)
            .rows(10)
            .build();

        return kopisFeignClient.getPerformances(performanceRequest);
    }

}

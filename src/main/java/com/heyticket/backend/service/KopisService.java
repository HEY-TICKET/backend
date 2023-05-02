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

    public List<PerformanceResponse> getPerformanceFromKopis(LocalDate from, LocalDate to) {
        KopisPerformanceRequest performanceRequest = KopisPerformanceRequest.builder()
            .service(apiKey)
            .stdate(from.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .eddate(to.plusMonths(3).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .cpage(1)
            .rows(100)
            .build();

        return kopisFeignClient.getPerformances(performanceRequest);
    }

}

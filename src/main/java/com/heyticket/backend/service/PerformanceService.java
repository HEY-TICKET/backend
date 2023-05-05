package com.heyticket.backend.service;

import com.heyticket.backend.kopis.client.PerformanceRequest;
import com.heyticket.backend.kopis.client.PerformanceResponse;
import com.heyticket.backend.repository.PerformanceRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    private final KopisService kopisService;

    public void updatePerformances() {
        PerformanceRequest performanceRequest = PerformanceRequest.builder()
            .stdate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .eddate(LocalDate.now().plusMonths(3).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .cpage(1)
            .rows(9999)
            .build();

        List<PerformanceResponse> performanceResponseList = kopisService.getPerformance(performanceRequest);
    }

}

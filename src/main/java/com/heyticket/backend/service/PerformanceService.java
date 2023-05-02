package com.heyticket.backend.service;

import com.heyticket.backend.performances.client.PerformanceResponse;
import com.heyticket.backend.performances.domain.Performance;
import com.heyticket.backend.repository.PerformanceRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    private final KopisService kopisService;

    public void savePerformance() {
        List<PerformanceResponse> performanceResponseList = kopisService.getPerformanceFromKopis(LocalDate.now().minusMonths(2), LocalDate.now().plusDays(2));
        List<Performance> performanceList = performanceResponseList.stream()
            .map(PerformanceResponse::toEntity)
            .collect(Collectors.toList());
        performanceRepository.saveAll(performanceList);
    }

}

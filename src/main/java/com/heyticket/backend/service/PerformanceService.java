package com.heyticket.backend.service;

import com.heyticket.backend.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    private final KopisService kopisService;

    public void savePerformance() {
//        List<PerformanceResponse> performanceResponseList = kopisService.getPerformance(LocalDate.now().minusMonths(2), LocalDate.now().plusDays(2));
//        List<Performance> performanceList = performanceResponseList.stream()
//            .map(PerformanceResponse::toEntity)
//            .collect(Collectors.toList());
//        performanceRepository.saveAll(performanceList);
    }

}

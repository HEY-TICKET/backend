package com.heyticket.backend.schedule;

import com.heyticket.backend.service.PerformanceService;
import com.heyticket.backend.service.PlaceService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final PerformanceService performanceService;

    private final PlaceService placeService;

    @Value("${kopis.performance.batch-count: 200}")
    private int performanceBatchCount;

    @Scheduled(cron = "10 0 0 * * *")
    public void updatePerformanceState() {
        performanceService.updatePerformanceStatusBatch();
    }

    @Scheduled(cron = "0 0 0/3 * * *")
    public void updatePerformances() {
        performanceService.updatePerformancesBatch(LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(5), performanceBatchCount);
    }

    @Scheduled(cron = "0 0 12 * * *")
    public void updateBoxOfficeRank() {
        performanceService.updateBoxOfficeRankBatch();
    }

    @Scheduled(cron = "10 0 0/3 * * *")
    public void updatePlace() {
        placeService.updatePlacesBatch();
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void updateMeiliData() {
        performanceService.updatePerformanceMeiliData();
    }
}

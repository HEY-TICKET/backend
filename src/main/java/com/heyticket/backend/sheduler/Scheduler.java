package com.heyticket.backend.sheduler;

import com.heyticket.backend.service.PerformanceService;
import com.heyticket.backend.service.PlaceService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@EnableScheduling
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final PerformanceService performanceService;

    private final PlaceService placeService;

    @Scheduled(cron = "0 10 0/3 * * *")
    public void updatePerformanceState() {
        performanceService.updatePerformanceStatusBatch();
        log.info("Performance state update has been scheduled.");
    }

    @Scheduled(cron = "0 20 0/3 * * *")
    public void updatePerformances() {
        performanceService.updatePerformancesBatch(LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(5), 1000);
        log.info("Performances update has been scheduled.");
    }

    @Scheduled(cron = "0 30 0/3 * * *")
    public void updateBoxOfficeRank() {
        performanceService.updateBoxOfficeRankBatch();
        log.info("BoxOfficeRank update has been scheduled.");
    }

    @Scheduled(cron = "0 40 0/3 * * *")
    public void updatePlace() {
        placeService.updatePlacesBatch();
        log.info("Places update has been scheduled.");
    }
}

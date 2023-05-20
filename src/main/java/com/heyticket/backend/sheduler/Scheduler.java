package com.heyticket.backend.sheduler;

import com.heyticket.backend.service.PerformanceService;
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

    @Scheduled(cron = "0 0 0 * * *")
    public void updatePerformanceState() {
        performanceService.updatePerformanceState();
        log.info("The performance status update has been scheduled.");
    }

}

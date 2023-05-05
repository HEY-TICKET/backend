package com.heyticket.backend.service;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PerformanceServiceTest {

    @Autowired
    private PerformanceService performanceService;

    @Test
    void savePerformance() {
        performanceService.updatePerformances(LocalDate.now(), LocalDate.now().plusDays(1));
    }

}
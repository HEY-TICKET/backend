package com.heyticket.backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PerformanceServiceTest {

    @Autowired
    private PerformanceService performanceService;

    @Test
    void savePerformance() {
        performanceService.savePerformance();
    }

}
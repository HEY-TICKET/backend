package com.heyticket.backend.service;

import com.heyticket.backend.performances.client.PerformanceResponse;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KopisServiceTest {

    @Autowired
    private KopisService kopisService;

    @Test
    void getPerformances() {
        List<PerformanceResponse> performanceFromKopis = kopisService.getPerformanceFromKopis(LocalDate.now().minusMonths(2), LocalDate.now().plusDays(2));
        System.out.println("performanceFromKopis.size() = " + performanceFromKopis.size());
    }

}
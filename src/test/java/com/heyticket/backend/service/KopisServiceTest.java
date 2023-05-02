package com.heyticket.backend.service;

import com.heyticket.backend.performances.client.PerformanceResponse;
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
        List<PerformanceResponse> performanceFromKopis = kopisService.getPerformanceFromKopis();
        System.out.println("performanceFromKopis.size() = " + performanceFromKopis.size());
    }

}
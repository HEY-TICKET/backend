package com.heyticket.backend.service;

import com.heyticket.backend.module.kopis.client.dto.BoxOfficeResponse;
import java.time.LocalDate;
import java.util.List;
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
    
    @Test
    void getUniBoxOffice() {
        List<BoxOfficeResponse> uniBoxOffice = performanceService.getUniBoxOffice();
        System.out.println("uniBoxOffice.size() = " + uniBoxOffice.size());
    }

}
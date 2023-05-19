package com.heyticket.backend.service;

import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.repository.PerformanceRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PerformanceServiceTest {

    @Autowired
    private PerformanceService performanceService;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Test
    void savePerformance() {
        performanceService.updatePerformances(LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(12), 3000);
    }

    @Test
    void getUniBoxOffice() {
//        List<BoxOfficeResponse> uniBoxOffice = performanceService.getUniBoxOffice();
//        System.out.println("uniBoxOffice.size() = " + uniBoxOffice.size());
    }

    @Test
    void updateBoxOfficeRank() {
        performanceService.updateBoxOfficeRank();
    }

    @Test
    void createPrice() {
        List<Performance> performanceList = performanceRepository.findAll();
        for (Performance performance : performanceList) {
            String price = performance.getPrice();
            System.out.println(price + " -> ");
            parsePrice(price);
        }
    }

    private int parsePrice(String price) {
        String replace = price.replace(",", "");
        String[] splitString = replace.split(" ");
        for (String str : splitString) {
            if (str.endsWith("원")) {
                String substring = str.substring(0, str.length() - 1);
                if (!substring.endsWith("0")) {
                    continue;
                }
                System.out.println("parsed price = " + Integer.parseInt(substring));
//                return Integer.parseInt(substring);
            }
        }
        return 0;
    }

}
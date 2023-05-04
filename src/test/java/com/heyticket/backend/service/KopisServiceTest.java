package com.heyticket.backend.service;

import com.heyticket.backend.kopis.client.BoxOfficeRequest;
import com.heyticket.backend.kopis.client.PerformanceRequest;
import com.heyticket.backend.kopis.client.PerformanceResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class KopisServiceTest {

    @Autowired
    private KopisService kopisService;

    @Test
    void getPerformances() {
        //given
        PerformanceRequest performanceRequest = PerformanceRequest.builder()
            .stdate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .eddate(LocalDate.now().plusMonths(3).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .cpage(1)
            .rows(10)
            .build();

        //when
        List<PerformanceResponse> performanceFromKopis = kopisService.getPerformance(performanceRequest);

        //then
        System.out.println("performanceFromKopis.size() = " + performanceFromKopis.size());
    }

    @Test
    void getBoxOffice() {
        //given
        BoxOfficeRequest boxOfficeRequest = BoxOfficeRequest.builder()
            .ststype("day")
            .date(LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .build();

        //when
        List<BoxOfficeRequest> boxOffice = kopisService.getBoxOffice(boxOfficeRequest);

        //then
        System.out.println("boxOffice.size() = " + boxOffice.size());
    }

}
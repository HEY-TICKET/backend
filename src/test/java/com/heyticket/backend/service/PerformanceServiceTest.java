package com.heyticket.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.domain.Place;
import com.heyticket.backend.repository.PerformanceRepository;
import com.heyticket.backend.repository.PlaceRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PerformanceServiceTest {

    @Autowired
    private PerformanceService performanceService;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private PlaceRepository placeRepository;

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

    @Test
    void addViews() throws InterruptedException {
        Performance performance = Performance.builder()
            .id("id")
            .views(0)
            .placeId("placeId")
            .build();

        performanceRepository.save(performance);

        Place place = Place.builder()
            .id("placeId")
            .build();

        placeRepository.save(place);

        // 동시에 실행할 스레드 수
        int threadCount = 10;

        // 카운트다운 래치 생성 (모든 스레드 작업이 완료될 때까지 대기하기 위함)
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        // 스레드 작업 실행
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // PerformanceService의 getPerformanceById 메서드 호출
                    performanceService.getPerformanceById(performance.getId());
                } finally {
                    latch.countDown();
                }
            });
//            Thread.sleep(500);
        }

        // 모든 스레드 작업이 완료될 때까지 대기
        latch.await();

        // Performance 엔티티 조회
        Performance foundPerformance = performanceRepository.findById(performance.getId())
            .orElseThrow(() -> new NoSuchElementException("no such performance. performanceId : " + performance.getId()));

        // 검증: views 값이 스레드 작업 횟수와 일치하는지 확인
        int expectedViews = threadCount;
        int actualViews = foundPerformance.getViews();
        assertThat(actualViews).isEqualTo(expectedViews);
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
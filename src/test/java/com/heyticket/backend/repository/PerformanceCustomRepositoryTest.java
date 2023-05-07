//package com.heyticket.backend.repository;
//
//import com.heyticket.backend.domain.Performance;
//import java.util.Optional;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//class PerformanceCustomRepositoryTest {
//
//    @Autowired
//    private PerformanceRepository performanceRepository;
//
//    @Test
//    void findById() {
//        //given
//        Performance performance = Performance.builder()
//            .id("testId")
//            .title("title")
//            .build();
//
//        //when
//        performanceRepository.save(performance);
//
//        //then
//        Optional<Performance> byId = performanceRepository.findById(performance.getId());
//        Assertions.assertThat(byId).isPresent();
//    }
//
//}
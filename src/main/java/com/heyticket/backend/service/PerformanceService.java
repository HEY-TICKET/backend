package com.heyticket.backend.service;

import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.module.kopis.client.dto.BoxOfficeResponse;
import com.heyticket.backend.module.kopis.client.dto.KopisBoxOfficeRequest;
import com.heyticket.backend.module.kopis.client.dto.PerformanceDetailResponse;
import com.heyticket.backend.module.kopis.client.dto.PerformanceRequest;
import com.heyticket.backend.module.kopis.client.dto.PerformanceResponse;
import com.heyticket.backend.module.kopis.enums.TimePeriod;
import com.heyticket.backend.module.mapper.PerformanceMapper;
import com.heyticket.backend.repository.PerformanceRepository;
import com.heyticket.backend.service.dto.BoxOfficeRequest;
import com.heyticket.backend.service.dto.PerformanceDto;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    private final KopisService kopisService;

    public void updatePerformances(LocalDate from, LocalDate to) {
        PerformanceRequest performanceRequest = PerformanceRequest.builder()
            .stdate(from.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .eddate(to.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .cpage(1)
            .rows(100000)
            .build();

        List<PerformanceResponse> performanceResponseList = kopisService.getPerformances(performanceRequest);
        log.info("Performance pull count : {}", performanceResponseList.size());

        List<String> idList = performanceRepository.findAllIds();
        HashSet<String> idSet = new HashSet<>(idList);

        List<Performance> newPerformanceList = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            String performanceId = performanceResponseList.get(i).mt20id();
            if (!idSet.contains(performanceId)) {
                PerformanceDetailResponse performanceDetailResponse = kopisService.getPerformanceDetail(performanceId);
                Performance performance = performanceDetailResponse.toEntity();
                newPerformanceList.add(performance);
            }
        }

        performanceRepository.saveAll(newPerformanceList);
        log.info("Success to update performance list. size : {}", newPerformanceList.size());
    }

    public List<PerformanceDto> getNewPerformances() {
        List<Performance> performanceList = performanceRepository.findNewPerformances();
        return performanceList.stream()
            .map(PerformanceMapper.INSTANCE::toPerformanceDto)
            .collect(Collectors.toList());
    }

    public List<BoxOfficeResponse> getUniBoxOffice() {
        KopisBoxOfficeRequest kopisBoxOfficeRequest = KopisBoxOfficeRequest.builder()
            .ststype(TimePeriod.DAY.getValue())
            .date(LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .area("UNI")
            .build();

        return kopisService.getBoxOffice(kopisBoxOfficeRequest);
    }

    public PerformanceDto getPerformanceById(String id) {
        Performance performance = performanceRepository.findById(id).orElseThrow(() -> new NoSuchElementException("no such performance"));
        String storyUrls = performance.getStoryUrls();
        PerformanceDto performanceDto = PerformanceMapper.INSTANCE.toPerformanceDto(performance);
        performanceDto.setStoryUrls(List.of(storyUrls.split("\\|")));
        return performanceDto;
    }

    public List<BoxOfficeResponse> getBoxOffice(BoxOfficeRequest request) {
//        if (!request.getDate().matches("\\d{4}-\\d{2}-\\d{2}")) {
//            throw new IllegalStateException("Invalid date format.");
//        }
        return kopisService.getBoxOffice(request.toKopisBoxOfficeRequest());
    }

}

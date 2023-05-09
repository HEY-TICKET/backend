package com.heyticket.backend.service;

import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.module.kopis.client.dto.KopisBoxOfficeResponse;
import com.heyticket.backend.module.kopis.client.dto.KopisBoxOfficeRequest;
import com.heyticket.backend.module.kopis.client.dto.KopisPerformanceDetailResponse;
import com.heyticket.backend.module.kopis.client.dto.KopisPerformanceRequest;
import com.heyticket.backend.module.kopis.client.dto.KopisPerformanceResponse;
import com.heyticket.backend.module.kopis.enums.TimePeriod;
import com.heyticket.backend.module.mapper.PerformanceMapper;
import com.heyticket.backend.repository.PerformanceRepository;
import com.heyticket.backend.service.dto.BoxOfficeRequest;
import com.heyticket.backend.service.dto.CommonResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        KopisPerformanceRequest kopisPerformanceRequest = KopisPerformanceRequest.builder()
            .stdate(from.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .eddate(to.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .cpage(1)
            .rows(100000)
            .build();

        List<KopisPerformanceResponse> kopisPerformanceResponseList = kopisService.getPerformances(kopisPerformanceRequest);
        log.info("Performance pull count : {}", kopisPerformanceResponseList.size());

        List<String> allIdList = performanceRepository.findAllIds();
        HashSet<String> allIdSet = new HashSet<>(allIdList);

        List<Performance> newPerformanceList = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            String performanceId = kopisPerformanceResponseList.get(i).mt20id();
            if (!allIdSet.contains(performanceId)) {
                KopisPerformanceDetailResponse kopisPerformanceDetailResponse = kopisService.getPerformanceDetail(performanceId);
                Performance performance = kopisPerformanceDetailResponse.toEntity();
                newPerformanceList.add(performance);
            }
        }

        performanceRepository.saveAll(newPerformanceList);
        log.info("Success to update performance list. size : {}", newPerformanceList.size());
    }

    public CommonResponse<List<PerformanceDto>> getNewPerformances() {
        List<Performance> performanceList = performanceRepository.findNewPerformances();
        List<PerformanceDto> performanceDtoList = performanceList.stream()
            .map(performance -> {
                PerformanceDto performanceDto = PerformanceMapper.INSTANCE.toPerformanceDto(performance);
                performanceDto.updateStoryUrls(performance.getStoryUrls());
                return performanceDto;
            })
            .collect(Collectors.toList());

        return CommonResponse.ok("message", performanceDtoList);
    }

    public ResponseEntity<List<KopisBoxOfficeResponse>> getUniBoxOffice() {
        KopisBoxOfficeRequest kopisBoxOfficeRequest = KopisBoxOfficeRequest.builder()
            .ststype(TimePeriod.DAY.getValue())
            .date(LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .area("UNI")
            .build();

        List<KopisBoxOfficeResponse> kopisBoxOfficeResponseList = kopisService.getBoxOffice(kopisBoxOfficeRequest);

        return new ResponseEntity<>(kopisBoxOfficeResponseList, HttpStatus.OK);
    }

    public PerformanceDto getPerformanceById(String id) {
        Performance performance = performanceRepository.findById(id).orElseThrow(() -> new NoSuchElementException("no such performance"));
        PerformanceDto performanceDto = PerformanceMapper.INSTANCE.toPerformanceDto(performance);
        performanceDto.updateStoryUrls(performance.getStoryUrls());
        return performanceDto;
    }

    public ResponseEntity<List<KopisBoxOfficeResponse>> getBoxOffice(BoxOfficeRequest request) {
//        if (!request.getDate().matches("\\d{4}-\\d{2}-\\d{2}")) {
//            throw new IllegalStateException("Invalid date format.");
//        }
        List<KopisBoxOfficeResponse> kopisBoxOfficeResponseList = kopisService.getBoxOffice(request.toKopisBoxOfficeRequest());
        return new ResponseEntity<>(kopisBoxOfficeResponseList, HttpStatus.OK);
    }

}

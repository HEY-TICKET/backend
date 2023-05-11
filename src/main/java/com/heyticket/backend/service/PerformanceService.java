package com.heyticket.backend.service;

import com.heyticket.backend.domain.BoxOfficeRank;
import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.module.kopis.client.dto.KopisBoxOfficeRequest;
import com.heyticket.backend.module.kopis.client.dto.KopisBoxOfficeResponse;
import com.heyticket.backend.module.kopis.client.dto.KopisPerformanceDetailResponse;
import com.heyticket.backend.module.kopis.client.dto.KopisPerformanceRequest;
import com.heyticket.backend.module.kopis.client.dto.KopisPerformanceResponse;
import com.heyticket.backend.module.kopis.enums.BoxOfficeArea;
import com.heyticket.backend.module.kopis.enums.BoxOfficeGenre;
import com.heyticket.backend.module.kopis.enums.TimePeriod;
import com.heyticket.backend.module.mapper.PerformanceMapper;
import com.heyticket.backend.repository.BoxOfficeRankRepository;
import com.heyticket.backend.repository.PerformanceRepository;
import com.heyticket.backend.service.dto.BoxOfficeRankRequest;
import com.heyticket.backend.service.dto.BoxOfficeRankResponse;
import com.heyticket.backend.service.dto.NewPerformanceRequest;
import com.heyticket.backend.service.dto.PerformanceResponse;
import com.heyticket.backend.service.dto.pagable.PageResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    private final BoxOfficeRankRepository boxOfficeRankRepository;

    private final KopisService kopisService;

    public void updatePerformances(LocalDate from, LocalDate to, int rows) {
        KopisPerformanceRequest kopisPerformanceRequest = KopisPerformanceRequest.builder()
            .stdate(from.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .eddate(to.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .cpage(1)
            .rows(rows)
            .build();

        List<KopisPerformanceResponse> kopisPerformanceResponseList = kopisService.getPerformances(kopisPerformanceRequest);
        log.info("Performance pull count : {}", kopisPerformanceResponseList.size());

        List<String> allIdList = performanceRepository.findAllIds();
        HashSet<String> allIdSet = new HashSet<>(allIdList);

        List<Performance> newPerformanceList = new ArrayList<>();

        for (KopisPerformanceResponse kopisPerformanceResponse : kopisPerformanceResponseList) {
            String performanceId = kopisPerformanceResponse.mt20id();
            if (!allIdSet.contains(performanceId)) {
                KopisPerformanceDetailResponse kopisPerformanceDetailResponse = kopisService.getPerformanceDetail(performanceId);
                Performance performance = kopisPerformanceDetailResponse.toEntity();
                newPerformanceList.add(performance);
            }
        }

        performanceRepository.saveAll(newPerformanceList);
        log.info("Success to update performance list. size : {}", newPerformanceList.size());
    }

    public PageResponse<PerformanceResponse> getNewPerformances(NewPerformanceRequest newPerformanceRequest, Pageable pageable) {
        Page<Performance> performancePageResponse = performanceRepository.findNewPerformances(newPerformanceRequest, pageable);
        List<Performance> performanceList = performancePageResponse.getContent();
        List<PerformanceResponse> performanceResponseList = performanceList.stream()
            .map(performance -> {
                PerformanceResponse performanceResponse = PerformanceMapper.INSTANCE.toPerformanceDto(performance);
                performanceResponse.updateStoryUrls(performance.getStoryUrls());
                return performanceResponse;
            })
            .collect(Collectors.toList());

        return new PageResponse<>(performanceResponseList, pageable.getPageSize() + 1, pageable.getPageNumber(), performancePageResponse.getTotalPages());
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

    public PerformanceResponse getPerformanceById(String id) {
        Performance performance = performanceRepository.findById(id).orElseThrow(() -> new NoSuchElementException("no such performance"));
        PerformanceResponse performanceResponse = PerformanceMapper.INSTANCE.toPerformanceDto(performance);
        performanceResponse.updateStoryUrls(performance.getStoryUrls());
        return performanceResponse;
    }

    public PageResponse<BoxOfficeRankResponse> getBoxOfficeRank(BoxOfficeRankRequest request, Pageable pageable) {
        BoxOfficeRank boxOfficeRank = boxOfficeRankRepository.findBoxOfficeRank(request)
            .orElseThrow(() -> new NoSuchElementException("No such boxOfficeRank."));

        int dataSize = pageable.getPageSize();
        String[] idArray = boxOfficeRank.getPerformanceIds().split("\\|");
        String[] truncatedIdArray = Arrays.copyOfRange(idArray, 0, dataSize);

        List<Performance> performanceList = performanceRepository.findAllById(Arrays.asList(truncatedIdArray));
        Map<String, Performance> performanceMap = new HashMap<>();
        performanceList.forEach(performance -> performanceMap.put(performance.getId(), performance));

        PerformanceMapper performanceMapper = PerformanceMapper.INSTANCE;

        List<BoxOfficeRankResponse> boxOfficeRankResponseList = new ArrayList<>();
        List<Performance> unsavedPerformanceList = new ArrayList<>();

        for (int i = 0; i < truncatedIdArray.length; i++) {
            String performanceId = truncatedIdArray[i];
            Performance performance = performanceMap.get(performanceId);
            if (ObjectUtils.isEmpty(performance)) {
                KopisPerformanceDetailResponse kopisPerformanceDetailResponse = kopisService.getPerformanceDetail(performanceId);
                performance = kopisPerformanceDetailResponse.toEntity();
                unsavedPerformanceList.add(performance);
            }
            BoxOfficeRankResponse boxOfficeRankResponse = performanceMapper.toBoxOfficeRankResponse(performance);
            boxOfficeRankResponse.setRank(i + 1);
            boxOfficeRankResponse.updateStoryUrls(performance.getStoryUrls());
            boxOfficeRankResponseList.add(boxOfficeRankResponse);
        }

        performanceRepository.saveAll(unsavedPerformanceList);

        return new PageResponse<>(boxOfficeRankResponseList, 1, dataSize, 1);
    }

    public void updateBoxOfficeRank() {
        BoxOfficeGenre[] genres = BoxOfficeGenre.values();
        BoxOfficeArea[] areas = BoxOfficeArea.values();
        TimePeriod[] timePeriods = TimePeriod.values();
        List<BoxOfficeRank> boxOfficeRankList = new ArrayList<>();
        for (BoxOfficeGenre genre : genres) {
            for (BoxOfficeArea area : areas) {
                for (TimePeriod timePeriod : timePeriods) {
                    KopisBoxOfficeRequest kopisBoxOfficeRequest = KopisBoxOfficeRequest.builder()
                        .ststype(timePeriod.getValue())
                        .date(LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                        .catecode(genre.getCode())
                        .area(area.getValue())
                        .build();

                    List<KopisBoxOfficeResponse> kopisBoxOfficeResponseList = kopisService.getBoxOffice(kopisBoxOfficeRequest);

                    String ids = kopisBoxOfficeResponseList.stream()
                        .map(KopisBoxOfficeResponse::mt20id)
                        .collect(Collectors.joining("|"));

                    BoxOfficeRank boxOfficeRank = BoxOfficeRank.builder()
                        .genre(genre)
                        .area(area)
                        .timePeriod(timePeriod)
                        .performanceIds(ids)
                        .build();

                    boxOfficeRankList.add(boxOfficeRank);
                }
            }
        }

        boxOfficeRankRepository.saveAll(boxOfficeRankList);
    }

}

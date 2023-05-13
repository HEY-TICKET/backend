package com.heyticket.backend.service;

import com.heyticket.backend.domain.BoxOfficeRank;
import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.domain.Place;
import com.heyticket.backend.module.kopis.client.dto.KopisBoxOfficeRequest;
import com.heyticket.backend.module.kopis.client.dto.KopisBoxOfficeRequest.KopisBoxOfficeRequestBuilder;
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
import com.heyticket.backend.repository.PlaceRepository;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final PlaceRepository placeRepository;

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
        log.info("Performance has been updated. updated size : {}, total size : {}", newPerformanceList.size(), kopisPerformanceResponseList.size());
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

        return new PageResponse<>(performanceResponseList, pageable.getPageNumber() + 1, pageable.getPageSize() , performancePageResponse.getTotalPages());
    }

    public PerformanceResponse getPerformanceById(String performanceId) {
        Performance performance = performanceRepository.findById(performanceId).orElseThrow(() -> new NoSuchElementException("no such performance. performanceId : " + performanceId));
        performance.addViews();
        return getPerformanceResponse(performance);
    }

    public PerformanceResponse getPerformanceByIdWithoutViewCount(String performanceId) {
        Performance performance = performanceRepository.findById(performanceId).orElseThrow(() -> new NoSuchElementException("no such performance. performanceId : " + performanceId));
        return getPerformanceResponse(performance);
    }

    private PerformanceResponse getPerformanceResponse(Performance performance) {
        PerformanceResponse performanceResponse = PerformanceMapper.INSTANCE.toPerformanceDto(performance);
        performanceResponse.updateStoryUrls(performance.getStoryUrls());

        Place place = placeRepository.findById(performance.getPlaceId()).orElseThrow(() -> new NoSuchElementException("no such place. performanceId : " + performance.getPlaceId()));
        performanceResponse.updateLocation(place.getLatitude(), place.getLatitude());
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

    public List<PerformanceResponse> getPerformanceRecommendation(String performanceId) {
        Performance performance = performanceRepository.findById(performanceId).orElseThrow(() -> new NoSuchElementException("no such performance. performanceId : " + performanceId));
        BoxOfficeGenre boxOfficeGenre = BoxOfficeGenre.getByName(performance.getGenre());

        BoxOfficeRankRequest genreBoxOfficeRankRequest = BoxOfficeRankRequest.builder()
            .genre(boxOfficeGenre)
            .timePeriod(TimePeriod.WEEK)
            .build();

        BoxOfficeRank genreBoxOfficeRank = boxOfficeRankRepository.findBoxOfficeRank(genreBoxOfficeRankRequest).orElseThrow(() -> new NoSuchElementException("no such boxOfficeRank."));
        String[] genrePerformanceIdArray = genreBoxOfficeRank.getPerformanceIds().split("\\|");
        String firstGenrePerformanceId = genrePerformanceIdArray[0];
        String secondGenrePerformanceId = genrePerformanceIdArray[1];

        String placeId = performance.getPlaceId();
        Place place = placeRepository.findById(placeId).orElseThrow(() -> new NoSuchElementException("no such place. id : " + placeId));
        String sidoName = place.getSidoName();
        BoxOfficeArea boxOfficeArea = BoxOfficeArea.getByName(sidoName);

        BoxOfficeRankRequest areaBoxOfficeRankRequest = BoxOfficeRankRequest.builder()
            .area(boxOfficeArea)
            .timePeriod(TimePeriod.WEEK)
            .build();

        BoxOfficeRank areaBoxOfficeRank = boxOfficeRankRepository.findBoxOfficeRank(areaBoxOfficeRankRequest).orElseThrow(() -> new NoSuchElementException("no such boxOfficeRank."));
        String[] areaPerformanceIdArray = areaBoxOfficeRank.getPerformanceIds().split("\\|");
        String firstAreaPerformanceId = areaPerformanceIdArray[0];
        String secondAreaPerformanceId = areaPerformanceIdArray[1];

        return List.of(getPerformanceByIdWithoutViewCount(firstGenrePerformanceId),
            getPerformanceByIdWithoutViewCount(secondGenrePerformanceId),
            getPerformanceByIdWithoutViewCount(firstAreaPerformanceId),
            getPerformanceByIdWithoutViewCount(secondAreaPerformanceId));
    }

    public void updateBoxOfficeRank() {
        boxOfficeRankRepository.deleteAll();
        BoxOfficeGenre[] genres = BoxOfficeGenre.values();
        BoxOfficeArea[] areas = BoxOfficeArea.values();
        TimePeriod[] timePeriods = TimePeriod.values();

        List<CompletableFuture<BoxOfficeRank>> futures = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(20);

        for (TimePeriod timePeriod : timePeriods) {
            for (BoxOfficeGenre genre : genres) {
                CompletableFuture<BoxOfficeRank> future = CompletableFuture.supplyAsync(() -> {
                    KopisBoxOfficeRequestBuilder kopisBoxOfficeRequestBuilder = KopisBoxOfficeRequest.builder()
                        .ststype(timePeriod.getValue())
                        .date(LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                    if (genre != BoxOfficeGenre.ALL) {
                        kopisBoxOfficeRequestBuilder.catecode(genre.getCode());
                    }
                    KopisBoxOfficeRequest kopisBoxOfficeRequest = kopisBoxOfficeRequestBuilder.build();

                    List<KopisBoxOfficeResponse> kopisBoxOfficeResponseList = kopisService.getBoxOffice(kopisBoxOfficeRequest);

                    String ids = kopisBoxOfficeResponseList.stream()
                        .map(KopisBoxOfficeResponse::mt20id)
                        .collect(Collectors.joining("|"));

                    return BoxOfficeRank.builder()
                        .genre(genre != BoxOfficeGenre.ALL ? genre : null)
                        .timePeriod(timePeriod)
                        .performanceIds(ids)
                        .build();
                }, executorService);
                futures.add(future);

            }

            for (BoxOfficeArea area : areas) {
                CompletableFuture<BoxOfficeRank> future = CompletableFuture.supplyAsync(() -> {
                    KopisBoxOfficeRequestBuilder kopisBoxOfficeRequestBuilder = KopisBoxOfficeRequest.builder()
                        .ststype(timePeriod.getValue())
                        .date(LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                    if (area != BoxOfficeArea.ALL) {
                        kopisBoxOfficeRequestBuilder.area(area.getCode());
                    }
                    KopisBoxOfficeRequest kopisBoxOfficeRequest = kopisBoxOfficeRequestBuilder.build();

                    List<KopisBoxOfficeResponse> kopisBoxOfficeResponseList = kopisService.getBoxOffice(kopisBoxOfficeRequest);

                    String ids = kopisBoxOfficeResponseList.stream()
                        .map(KopisBoxOfficeResponse::mt20id)
                        .collect(Collectors.joining("|"));

                    return BoxOfficeRank.builder()
                        .area(area != BoxOfficeArea.ALL ? area : null)
                        .timePeriod(timePeriod)
                        .performanceIds(ids)
                        .build();
                }, executorService);
                futures.add(future);
            }
        }

        List<BoxOfficeRank> boxOfficeRankList = futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());

        boxOfficeRankRepository.saveAll(boxOfficeRankList);
        log.info("box office rank has been updated. size : {}", boxOfficeRankList.size());
    }

}

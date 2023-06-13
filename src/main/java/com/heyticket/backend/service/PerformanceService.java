package com.heyticket.backend.service;

import com.heyticket.backend.domain.BoxOfficeRank;
import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.domain.PerformancePrice;
import com.heyticket.backend.domain.Place;
import com.heyticket.backend.domain.enums.PerformanceStatus;
import com.heyticket.backend.module.kopis.client.dto.KopisBoxOfficeRequest;
import com.heyticket.backend.module.kopis.client.dto.KopisBoxOfficeResponse;
import com.heyticket.backend.module.kopis.client.dto.KopisPerformanceDetailResponse;
import com.heyticket.backend.module.kopis.client.dto.KopisPerformanceRequest;
import com.heyticket.backend.module.kopis.client.dto.KopisPerformanceResponse;
import com.heyticket.backend.module.kopis.enums.Area;
import com.heyticket.backend.module.kopis.enums.BoxOfficeArea;
import com.heyticket.backend.module.kopis.enums.BoxOfficeGenre;
import com.heyticket.backend.module.kopis.enums.Genre;
import com.heyticket.backend.module.kopis.enums.TimePeriod;
import com.heyticket.backend.module.kopis.service.KopisService;
import com.heyticket.backend.module.mapper.PerformanceMapper;
import com.heyticket.backend.repository.BoxOfficeRankRepository;
import com.heyticket.backend.repository.PerformancePriceRepository;
import com.heyticket.backend.repository.PerformanceRepository;
import com.heyticket.backend.repository.PlaceRepository;
import com.heyticket.backend.service.dto.pagable.PageResponse;
import com.heyticket.backend.service.dto.request.BoxOfficeRankRequest;
import com.heyticket.backend.service.dto.request.NewPerformanceRequest;
import com.heyticket.backend.service.dto.request.PerformanceFilterRequest;
import com.heyticket.backend.service.dto.request.PerformanceSearchRequest;
import com.heyticket.backend.service.dto.response.BoxOfficeRankResponse;
import com.heyticket.backend.service.dto.response.GenreCountResponse;
import com.heyticket.backend.service.dto.response.PerformanceResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
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
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    private final PerformancePriceRepository performancePriceRepository;

    private final BoxOfficeRankRepository boxOfficeRankRepository;

    private final PlaceRepository placeRepository;

    private final KopisService kopisService;

    @Transactional(readOnly = true)
    public PageResponse<PerformanceResponse> getPerformancesByCondition(PerformanceFilterRequest request, Pageable pageable) {
        Page<Performance> performancePageResponse = performanceRepository.findPerformanceByCondition(request, pageable);
        List<Performance> performanceList = performancePageResponse.getContent();
        List<PerformanceResponse> performanceResponseList = performanceList.stream()
            .map(this::getPerformanceResponse)
            .collect(Collectors.toList());

        return new PageResponse<>(performanceResponseList, pageable.getPageNumber() + 1, performancePageResponse.getNumberOfElements(), performancePageResponse.getTotalPages());
    }

    @Transactional(readOnly = true)
    public PageResponse<PerformanceResponse> searchPerformances(PerformanceSearchRequest request, Pageable pageable) {
        Page<Performance> performancePageResponse = performanceRepository.findPerformanceBySearchQuery(request, pageable);
        List<Performance> performanceList = performancePageResponse.getContent();
        List<PerformanceResponse> performanceResponseList = performanceList.stream()
            .map(this::getPerformanceResponse)
            .collect(Collectors.toList());

        return new PageResponse<>(performanceResponseList, pageable.getPageNumber() + 1, performancePageResponse.getNumberOfElements(), performancePageResponse.getTotalPages());
    }

    @Transactional(readOnly = true)
    public PageResponse<PerformanceResponse> getNewPerformances(NewPerformanceRequest newPerformanceRequest, Pageable pageable) {
        Page<Performance> performancePageResponse = performanceRepository.findNewPerformances(newPerformanceRequest, pageable);
        List<Performance> performanceList = performancePageResponse.getContent();
        List<PerformanceResponse> performanceResponseList = performanceList.stream()
            .map(this::getPerformanceResponse)
            .collect(Collectors.toList());

        return new PageResponse<>(performanceResponseList, pageable.getPageNumber() + 1, performancePageResponse.getNumberOfElements(), performancePageResponse.getTotalPages());
    }

    @Transactional(readOnly = true)
    public PerformanceResponse getPerformanceById(String performanceId) {
        Performance performance = getPerformanceFromDb(performanceId);
        performance.addViewCount();
        return getPerformanceResponse(performance);
    }

    @Transactional(readOnly = true)
    public PerformanceResponse getPerformanceByIdWithoutUpdatingViewCount(String performanceId) {
        Performance performance = getPerformanceFromDb(performanceId);
        return getPerformanceResponse(performance);
    }
    
    private PerformanceResponse getPerformanceResponse(Performance performance) {
        PerformanceResponse performanceResponse = PerformanceMapper.INSTANCE.toPerformanceDto(performance);
        performanceResponse.updateStoryUrls(performance.getStoryUrls());

        Place place = performance.getPlace();
        if (!ObjectUtils.isEmpty(place)) {
            performanceResponse.updateLocation(place.getLatitude(), place.getLongitude());
            performanceResponse.setAddress(place.getAddress());
            performanceResponse.setPhoneNumber(place.getPhoneNumber());
            performanceResponse.setPlaceId(place.getId());
            performanceResponse.setSido(place.getArea().getName());
            performanceResponse.setGugun(place.getGugunName());
        }
        return performanceResponse;
    }

    @Transactional(readOnly = true)
    public PageResponse<BoxOfficeRankResponse> getBoxOfficeRank(BoxOfficeRankRequest request, Pageable pageable) {
        BoxOfficeRank boxOfficeRank = boxOfficeRankRepository.findBoxOfficeRank(request)
            .orElseThrow(() -> new NoSuchElementException("No such boxOfficeRank."));

        if (!StringUtils.hasText(boxOfficeRank.getPerformanceIds())) {
            return new PageResponse<>(Collections.emptyList(), 1, 0, 1);
        }
        String[] performanceIds = boxOfficeRank.getPerformanceIds().split("\\|");

        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber() + 1;

        int totalItems = performanceIds.length;
        int totalPage = (int) Math.ceil((double) totalItems / pageSize);

        if (pageNumber > totalPage) {
            performanceIds = new String[]{};
        } else {
            int start = (pageNumber - 1) * pageSize;
            int end = Math.min(start + pageSize, totalItems);
            performanceIds = Arrays.copyOfRange(performanceIds, start, end);
        }

        List<Performance> performanceList = performanceRepository.findAllById(Arrays.asList(performanceIds));
        Map<String, Performance> performanceMap = new HashMap<>();
        performanceList.forEach(performance -> performanceMap.put(performance.getId(), performance));

        List<BoxOfficeRankResponse> boxOfficeRankResponseList = new ArrayList<>();
        List<Performance> unsavedPerformanceList = new ArrayList<>();

        for (int i = 0; i < performanceIds.length; i++) {
            String performanceId = performanceIds[i];
            Performance performance = performanceMap.get(performanceId);
            if (ObjectUtils.isEmpty(performance)) {
                KopisPerformanceDetailResponse kopisPerformanceDetailResponse = kopisService.getPerformanceDetail(performanceId);
                performance = kopisPerformanceDetailResponse.toEntity();
                unsavedPerformanceList.add(performance);
            }
            BoxOfficeRankResponse boxOfficeRankResponse = PerformanceMapper.INSTANCE.toBoxOfficeRankResponse(performance);
            boxOfficeRankResponse.setRank(i + 1);
            boxOfficeRankResponse.updateStoryUrls(performance.getStoryUrls());
            boxOfficeRankResponseList.add(boxOfficeRankResponse);
        }

        performanceRepository.saveAll(unsavedPerformanceList);

        return new PageResponse<>(boxOfficeRankResponseList, pageNumber, boxOfficeRankResponseList.size(), totalPage);
    }

    @Transactional(readOnly = true)
    public List<PerformanceResponse> getPerformanceRecommendation(String performanceId) {
        Performance performance = getPerformanceFromDb(performanceId);
        BoxOfficeGenre boxOfficeGenre = performance.getGenre().getBoxOfficeGenre();

        BoxOfficeRankRequest genreBoxOfficeRankRequest = BoxOfficeRankRequest.builder()
            .genre(boxOfficeGenre)
            .timePeriod(TimePeriod.WEEK)
            .build();

        BoxOfficeRank genreBoxOfficeRank = boxOfficeRankRepository.findBoxOfficeRank(genreBoxOfficeRankRequest).orElseThrow(() -> new NoSuchElementException("no such boxOfficeRank."));
        String[] genrePerformanceIdArray = genreBoxOfficeRank.getPerformanceIds().split("\\|");
        List<String> filteredGerePerformanceIds = Arrays.stream(genrePerformanceIdArray)
            .filter(id -> !id.equals(performanceId))
            .collect(Collectors.toList());
        String firstGenrePerformanceId = filteredGerePerformanceIds.get(0);
        String secondGenrePerformanceId = filteredGerePerformanceIds.get(1);

        Area area = performance.getArea();
        BoxOfficeArea boxOfficeArea = area.getBoxOfficeArea();

        BoxOfficeRankRequest areaBoxOfficeRankRequest = BoxOfficeRankRequest.builder()
            .area(boxOfficeArea)
            .timePeriod(TimePeriod.WEEK)
            .build();

        BoxOfficeRank areaBoxOfficeRank = boxOfficeRankRepository.findBoxOfficeRank(areaBoxOfficeRankRequest).orElseThrow(() -> new NoSuchElementException("no such boxOfficeRank."));
        String[] areaPerformanceIdArray = areaBoxOfficeRank.getPerformanceIds().split("\\|");
        List<String> filteredAreaPerformanceIds = Arrays.stream(areaPerformanceIdArray)
            .filter(id -> !id.equals(performanceId))
            .filter(id -> !id.equals(firstGenrePerformanceId))
            .filter(id -> !id.equals(secondGenrePerformanceId))
            .collect(Collectors.toList());
        String firstAreaPerformanceId = filteredAreaPerformanceIds.get(0);
        String secondAreaPerformanceId = filteredAreaPerformanceIds.get(1);

        return List.of(getPerformanceByIdWithoutUpdatingViewCount(firstGenrePerformanceId),
            getPerformanceByIdWithoutUpdatingViewCount(secondGenrePerformanceId),
            getPerformanceByIdWithoutUpdatingViewCount(firstAreaPerformanceId),
            getPerformanceByIdWithoutUpdatingViewCount(secondAreaPerformanceId));
    }

    public int updatePerformancesBatch(LocalDate from, LocalDate to, int rows) {
        log.info("[Batch] Batch updating performances.");
        KopisPerformanceRequest kopisPerformanceRequest = KopisPerformanceRequest.builder()
            .stdate(from.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .eddate(to.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .cpage(1)
            .rows(rows)
            .build();

        List<KopisPerformanceResponse> kopisPerformanceResponseList = kopisService.getPerformances(kopisPerformanceRequest);

        List<String> allIdList = performanceRepository.findAllIds();
        HashSet<String> allIdSet = new HashSet<>(allIdList);

        List<Performance> newPerformances = new ArrayList<>();

        for (KopisPerformanceResponse kopisPerformanceResponse : kopisPerformanceResponseList) {
            String performanceId = kopisPerformanceResponse.mt20id();
            if (!allIdSet.contains(performanceId)) {
                KopisPerformanceDetailResponse kopisPerformanceDetailResponse = kopisService.getPerformanceDetail(performanceId);
                Performance performance = kopisPerformanceDetailResponse.toEntity();
                String placeId = kopisPerformanceDetailResponse.mt10id();
                if (StringUtils.hasText(placeId)) {
                    Optional<Place> optionalPlace = placeRepository.findById(placeId);
                    if (optionalPlace.isEmpty()) {
                        log.info("PlaceId is empty for performance. placeId : {}", placeId);
                        continue;
                    }
                    Place place = optionalPlace.get();
                    Area area = place.getArea();
                    performance.updateArea(area);
                    performance.updatePlace(place);
                }
                newPerformances.add(performance);
            }
        }

        List<Performance> performances = performanceRepository.saveAll(newPerformances);
        List<PerformancePrice> performancePrices = performances.stream()
            .filter(performance -> StringUtils.hasText(performance.getPrice()))
            .flatMap(performance -> getPerformancePrice(performance).stream())
            .collect(Collectors.toList());

        performancePriceRepository.saveAll(performancePrices);
        log.info("[Batch] Performance has been updated. total size : {}, updated size : {}", kopisPerformanceResponseList.size(), newPerformances.size());
        return performances.size();
    }

    public int updateBoxOfficeRankBatch() {
        log.info("[Batch] Batch updating performance rank.");
        boxOfficeRankRepository.deleteAll();
        BoxOfficeGenre[] genres = BoxOfficeGenre.values();
        BoxOfficeArea[] areas = BoxOfficeArea.values();
        TimePeriod[] timePeriods = TimePeriod.values();

        List<CompletableFuture<BoxOfficeRank>> futures = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(20);

        for (TimePeriod timePeriod : timePeriods) {
            for (BoxOfficeGenre genre : genres) {
                if (genre == BoxOfficeGenre.ALL) {
                    continue;
                }
                CompletableFuture<BoxOfficeRank> future = CompletableFuture.supplyAsync(() -> {
                    KopisBoxOfficeRequest kopisBoxOfficeRequest = KopisBoxOfficeRequest.builder()
                        .ststype(timePeriod.getValue())
                        .date(LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                        .catecode(genre.getCode())
                        .build();

                    List<KopisBoxOfficeResponse> kopisBoxOfficeResponseList = kopisService.getBoxOffice(kopisBoxOfficeRequest);

                    String ids = kopisBoxOfficeResponseList.stream()
                        .map(KopisBoxOfficeResponse::mt20id)
                        .collect(Collectors.joining("|"));

                    return BoxOfficeRank.builder()
                        .boxOfficeGenre(genre)
                        .boxOfficeArea(BoxOfficeArea.ALL)
                        .timePeriod(timePeriod)
                        .performanceIds(ids)
                        .build();
                }, executorService);
                futures.add(future);

            }

            for (BoxOfficeArea area : areas) {
                if (area == BoxOfficeArea.ALL) {
                    continue;
                }
                CompletableFuture<BoxOfficeRank> future = CompletableFuture.supplyAsync(() -> {
                    KopisBoxOfficeRequest kopisBoxOfficeRequest = KopisBoxOfficeRequest.builder()
                        .ststype(timePeriod.getValue())
                        .date(LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                        .area(area.getCode())
                        .build();

                    List<KopisBoxOfficeResponse> kopisBoxOfficeResponseList = kopisService.getBoxOffice(kopisBoxOfficeRequest);

                    String ids = kopisBoxOfficeResponseList.stream()
                        .map(KopisBoxOfficeResponse::mt20id)
                        .collect(Collectors.joining("|"));

                    return BoxOfficeRank.builder()
                        .boxOfficeArea(area)
                        .boxOfficeGenre(BoxOfficeGenre.ALL)
                        .timePeriod(timePeriod)
                        .performanceIds(ids)
                        .build();
                }, executorService);
                futures.add(future);
            }

            CompletableFuture<BoxOfficeRank> future = CompletableFuture.supplyAsync(() -> {
                KopisBoxOfficeRequest kopisBoxOfficeRequest = KopisBoxOfficeRequest.builder()
                    .ststype(timePeriod.getValue())
                    .date(LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                    .build();

                List<KopisBoxOfficeResponse> kopisBoxOfficeResponseList = kopisService.getBoxOffice(kopisBoxOfficeRequest);

                String ids = kopisBoxOfficeResponseList.stream()
                    .map(KopisBoxOfficeResponse::mt20id)
                    .collect(Collectors.joining("|"));

                return BoxOfficeRank.builder()
                    .boxOfficeGenre(BoxOfficeGenre.ALL)
                    .boxOfficeArea(BoxOfficeArea.ALL)
                    .timePeriod(timePeriod)
                    .performanceIds(ids)
                    .build();
            }, executorService);
            futures.add(future);

        }

        List<BoxOfficeRank> boxOfficeRankList = futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());

        boxOfficeRankRepository.saveAll(boxOfficeRankList);
        log.info("[Batch] Box office rank has been updated. size : {}", boxOfficeRankList.size());
        return boxOfficeRankList.size();
    }

    public int updatePerformanceStatusBatch() {
        log.info("[Batch] Batch updating performance status.");
        List<Performance> performanceList = performanceRepository.findAll();
        int updateCnt = 0;
        for (Performance performance : performanceList) {
            LocalDate today = LocalDate.now();
            LocalDate startDate = performance.getStartDate();
            LocalDate endDate = performance.getEndDate();
            PerformanceStatus performanceStatus;
            if (today.isAfter(endDate)) {
                performanceStatus = PerformanceStatus.COMPLETED;
            } else if (today.isBefore(startDate)) {
                performanceStatus = PerformanceStatus.UPCOMING;
            } else {
                performanceStatus = PerformanceStatus.ONGOING;
            }

            if (performance.getStatus() != performanceStatus) {
                performance.updateStatus(performanceStatus);
                updateCnt++;
            }
        }
        log.info("[Batch] Performance state has been updated. updated count : {}", updateCnt);
        return updateCnt;
    }

    @Transactional(readOnly = true)
    public List<GenreCountResponse> getPerformanceGenreCount() {
        List<GenreCountResponse> performanceGenreCount = performanceRepository.findPerformanceGenreCount();
        Set<Genre> countedGenreSet = performanceGenreCount.stream()
            .map(GenreCountResponse::getGenre)
            .collect(Collectors.toSet());

        Arrays.stream(Genre.values())
            .filter(genre -> genre != Genre.ALL)
            .filter(genre -> !countedGenreSet.contains(genre))
            .map(genre -> new GenreCountResponse(genre, 0L))
            .forEach(performanceGenreCount::add);

        performanceGenreCount.sort(Comparator.comparing(GenreCountResponse::getCount).reversed()
            .thenComparing(GenreCountResponse::getGenre));

        return performanceGenreCount;
    }

    private List<PerformancePrice> getPerformancePrice(Performance performance) {
        String price = performance.getPrice();
        String replacedPrice = price.replace(",", "");
        String[] splitString = replacedPrice.split(" ");

        return Arrays.stream(splitString)
            .filter(str -> str.endsWith("00원"))
            .map(str -> PerformancePrice.builder()
                .price(Integer.parseInt(str.substring(0, str.length() - 1)))
                .performance(performance)
                .build())
            .collect(Collectors.toList());
    }

    private Performance getPerformanceFromDb(String performanceId) {
        return performanceRepository.findById(performanceId).orElseThrow(() -> new NoSuchElementException("no such performance. performanceId : " + performanceId));
    }
}

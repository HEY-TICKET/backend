package com.heyticket.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import com.heyticket.backend.config.JpaConfig;
import com.heyticket.backend.domain.BoxOfficeRank;
import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.domain.PerformancePrice;
import com.heyticket.backend.domain.Place;
import com.heyticket.backend.domain.enums.PerformanceStatus;
import com.heyticket.backend.module.kopis.enums.Area;
import com.heyticket.backend.module.kopis.enums.BoxOfficeArea;
import com.heyticket.backend.module.kopis.enums.BoxOfficeGenre;
import com.heyticket.backend.module.kopis.enums.Genre;
import com.heyticket.backend.module.kopis.enums.SortOrder;
import com.heyticket.backend.module.kopis.enums.SortType;
import com.heyticket.backend.module.kopis.enums.TimePeriod;
import com.heyticket.backend.module.kopis.service.KopisService;
import com.heyticket.backend.repository.BoxOfficeRankRepository;
import com.heyticket.backend.repository.PerformancePriceRepository;
import com.heyticket.backend.repository.PerformanceRepository;
import com.heyticket.backend.repository.PlaceRepository;
import com.heyticket.backend.service.dto.pagable.CustomPageRequest;
import com.heyticket.backend.service.dto.pagable.PageResponse;
import com.heyticket.backend.service.dto.request.BoxOfficeRankRequest;
import com.heyticket.backend.service.dto.request.NewPerformanceRequest;
import com.heyticket.backend.service.dto.request.PerformanceFilterRequest;
import com.heyticket.backend.service.dto.request.PerformanceSearchRequest;
import com.heyticket.backend.service.dto.response.BoxOfficeRankResponse;
import com.heyticket.backend.service.dto.response.GenreCountResponse;
import com.heyticket.backend.service.dto.response.PerformanceResponse;
import com.heyticket.backend.service.enums.SearchType;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(value = JpaConfig.class)
@ActiveProfiles("test")
class PerformanceServiceTest {

    private PerformanceService performanceService;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private BoxOfficeRankRepository boxOfficeRankRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PerformancePriceRepository performancePriceRepository;

    @Mock
    private KopisService kopisService;

    @BeforeEach
    void init() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("email", "password");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        performanceService = new PerformanceService(performanceRepository, performancePriceRepository, boxOfficeRankRepository, placeRepository, kopisService);
    }

    @AfterEach
    void deleteAll() {
        placeRepository.deleteAll();
        boxOfficeRankRepository.deleteAll();
        performanceRepository.deleteAll();
        performancePriceRepository.deleteAll();
    }

    @Test
    @DisplayName("NewPerformance 조회 - 장르 전체로 조회한다.")
    void getNewPerformance_everyGenre() {
        //given
        Performance performance1 = createPerformanceWithGenre("1", Genre.DANCE);
        Performance performance2 = createPerformanceWithGenre("2", Genre.CIRCUS_AND_MAGIC);
        Performance performance3 = createPerformanceWithGenre("3", Genre.MIXED_GENRE);
        Performance performance4 = createPerformanceWithGenre("4", Genre.POPULAR_DANCE);

        performanceRepository.saveAll(List.of(performance1, performance2, performance3, performance4));

        //when
        NewPerformanceRequest request = NewPerformanceRequest.builder()
            .genre(Genre.ALL)
            .build();

        CustomPageRequest customPageRequest = new CustomPageRequest(1, 10);
        PageRequest pageRequest = customPageRequest.of();

        PageResponse<PerformanceResponse> result = performanceService.getNewPerformances(request, pageRequest);

        //then
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getPageSize()).isEqualTo(result.getContents().size());
        assertThat(result.getContents()).hasSize(4);
        assertThat(result.getContents()).extracting("id")
            .containsOnly(performance1.getId(), performance2.getId(), performance3.getId(), performance4.getId());
    }

    @Test
    @DisplayName("NewPerformance 조회 - 특정 장르로 조회한다.")
    void getNewPerformance_searchByGenre() {
        //given
        Performance performance1 = createPerformanceWithGenre("1", Genre.DANCE);
        Performance performance2 = createPerformanceWithGenre("2", Genre.CIRCUS_AND_MAGIC);
        Performance performance3 = createPerformanceWithGenre("3", Genre.MIXED_GENRE);
        Performance performance4 = createPerformanceWithGenre("4", Genre.POPULAR_DANCE);

        performanceRepository.saveAll(List.of(performance1, performance2, performance3, performance4));

        //when
        NewPerformanceRequest request = NewPerformanceRequest.builder()
            .genre(Genre.MIXED_GENRE)
            .build();

        CustomPageRequest customPageRequest = new CustomPageRequest(1, 10);
        PageRequest pageRequest = customPageRequest.of();

        PageResponse<PerformanceResponse> result = performanceService.getNewPerformances(request, pageRequest);

        //then
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getPageSize()).isEqualTo(result.getContents().size());
        assertThat(result.getContents()).hasSize(1);
        assertThat(result.getContents().get(0).getId()).isEqualTo(performance3.getId());
    }

    @Test
    @DisplayName("NewPerformance 조회 - 조회수 내림차순으로 정렬한다.")
    void getNewPerformance_sortByType() {
        //given
        Performance performance1 = createPerformanceWithGenre("1", Genre.DANCE);
        Performance performance2 = createPerformanceWithGenre("2", Genre.CIRCUS_AND_MAGIC);
        Performance performance3 = createPerformanceWithGenre("3", Genre.MIXED_GENRE);
        Performance performance4 = createPerformanceWithGenre("4", Genre.POPULAR_DANCE);

        performance2.addViewCount();
        performance3.addViewCount();
        performance3.addViewCount();
        performance3.addViewCount();
        performance4.addViewCount();
        performance4.addViewCount();

        performanceRepository.saveAll(List.of(performance1, performance2, performance3, performance4));

        //when
        NewPerformanceRequest request = NewPerformanceRequest.builder()
            .genre(Genre.ALL)
            .sortType(SortType.VIEWS)
            .sortOrder(SortOrder.DESC)
            .build();

        CustomPageRequest customPageRequest = new CustomPageRequest(1, 10);
        PageRequest pageRequest = customPageRequest.of();

        PageResponse<PerformanceResponse> result = performanceService.getNewPerformances(request, pageRequest);

        //then
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getPageSize()).isEqualTo(result.getContents().size());
        assertThat(result.getContents()).hasSize(4);
        assertThat(result.getContents()).extracting("id")
            .containsExactly(performance3.getId(), performance4.getId(), performance2.getId(), performance1.getId());
    }

    @Test
    @DisplayName("NewPerformance 조회 - storyUrl 파싱을 확인한다.")
    void getNewPerformance_updateStoryUrl() {
        //given
        String firstUrl = "http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF214754_230310_0122231.jpg";
        String secondUrl = "http://www.kopis.or.kr/upload/pfmIntroImage/PF_PF214754_230310_0122230.jpg";

        Performance performance = Performance.builder()
            .id("id")
            .title("title")
            .storyUrls(firstUrl + "|" + secondUrl)
            .build();

        performanceRepository.save(performance);

        //when
        NewPerformanceRequest request = NewPerformanceRequest.builder()
            .genre(Genre.ALL)
            .build();

        CustomPageRequest customPageRequest = new CustomPageRequest(1, 10);
        PageRequest pageRequest = customPageRequest.of();

        PageResponse<PerformanceResponse> result = performanceService.getNewPerformances(request, pageRequest);

        //then
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getPageSize()).isEqualTo(result.getContents().size());
        assertThat(result.getContents()).hasSize(1);
        PerformanceResponse performanceResponse = result.getContents().get(0);
        List<String> storyUrls = performanceResponse.getStoryUrls();
        assertThat(storyUrls).hasSize(2);
        assertThat(storyUrls).containsExactly(firstUrl, secondUrl);
    }

    @Test
    @DisplayName("Performance 단일 조회 - 성공 데이터 확인.")
    void getPerformanceById_success() {
        //given
        double latitude = 0.1;
        double longitude = 0.2;

        Place place = Place.builder()
            .id("placeId")
            .latitude(latitude)
            .longitude(longitude)
            .area(Area.CHUNGBUK)
            .address("address")
            .phoneNumber("phoneNumber")
            .build();

        Place savedPlace = placeRepository.save(place);

        Performance performance = Performance.builder()
            .id("performanceId")
            .place(savedPlace)
            .title("title")
            .startDate(LocalDate.of(2023, 5, 1))
            .endDate(LocalDate.of(2023, 5, 2))
            .theater("theater")
            .cast("cast")
            .crew("crew")
            .runtime("runtime")
            .age("age")
            .company("company")
            .price("price")
            .poster("poster")
            .story("story")
            .genre(Genre.DANCE)
            .status(PerformanceStatus.ONGOING)
            .openRun(true)
            .area(Area.CHUNGBUK)
            .storyUrls("storyUrls")
            .schedule("schedule")
            .views(0)
            .build();

        performanceRepository.save(performance);

        //when
        PerformanceResponse result = performanceService.getPerformanceById(performance.getId());

        //then
        assertThat(result.getId()).isEqualTo(performance.getId());
        assertThat(result.getPlaceId()).isEqualTo(place.getId());
        assertThat(result.getTitle()).isEqualTo(performance.getTitle());
        assertThat(result.getStartDate()).isEqualTo(performance.getStartDate());
        assertThat(result.getEndDate()).isEqualTo(performance.getEndDate());
        assertThat(result.getTheater()).isEqualTo(performance.getTheater());
        assertThat(result.getCast()).isEqualTo(performance.getCast());
        assertThat(result.getCrew()).isEqualTo(performance.getCrew());
        assertThat(result.getRuntime()).isEqualTo(performance.getRuntime());
        assertThat(result.getAge()).isEqualTo(performance.getAge());
        assertThat(result.getCompany()).isEqualTo(performance.getCompany());
        assertThat(result.getPrice()).isEqualTo(performance.getPrice());
        assertThat(result.getPoster()).isEqualTo(performance.getPoster());
        assertThat(result.getStory()).isEqualTo(performance.getStory());
        assertThat(result.getGenre()).isEqualTo(performance.getGenre());
        assertThat(result.getStatus()).isEqualTo(performance.getStatus());
        assertThat(result.getOpenRun()).isEqualTo(performance.getOpenRun());
        assertThat(result.getStoryUrls()).hasSize(1);
        assertThat(result.getSchedule()).isEqualTo(performance.getSchedule());
        assertThat(result.getViews()).isEqualTo(performance.getViews() + 1);
        assertThat(result.getLatitude()).isEqualTo(place.getLatitude());
        assertThat(result.getLongitude()).isEqualTo(place.getLongitude());
        assertThat(result.getAddress()).isEqualTo(place.getAddress());
        assertThat(result.getPhoneNumber()).isEqualTo(place.getPhoneNumber());
    }

    @Test
    @DisplayName("Performance 단일 조회 - 해당 id가 없는 경우 NoSuchElementException을 throw한다.")
    void getPerformanceById_noSuchId() {
        //given

        //when
        Throwable throwable = catchThrowable(() -> performanceService.getPerformanceById("randomId"));

        //then
        assertThat(throwable).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("BoxOffice rank 조회 - 성공 데이터 확인.")
    void getBoxOfficeRank_success() {
        //given
        Performance performance2 = createPerformance("2");
        Performance performance1 = createPerformance("1");
        Performance performance3 = createPerformance("3");
        Performance performance4 = createPerformance("4");

        performanceRepository.saveAll(List.of(performance2, performance1, performance3, performance4));

        BoxOfficeRank boxOfficeRank = BoxOfficeRank.builder()
            .performanceIds(performance2.getId() + "|" + performance1.getId() + "|" + performance3.getId() + "|" + performance4.getId())
            .timePeriod(TimePeriod.DAY)
            .boxOfficeArea(BoxOfficeArea.BUSAN)
            .boxOfficeGenre(BoxOfficeGenre.MIXED_GENRE)
            .build();

        boxOfficeRankRepository.save(boxOfficeRank);

        //when
        BoxOfficeRankRequest request = BoxOfficeRankRequest.builder()
            .timePeriod(TimePeriod.DAY)
            .boxOfficeArea(BoxOfficeArea.BUSAN)
            .boxOfficeGenre(BoxOfficeGenre.MIXED_GENRE)
            .build();

        CustomPageRequest customPageRequest = new CustomPageRequest(1, 3);
        PageRequest pageRequest = customPageRequest.of();

        PageResponse<BoxOfficeRankResponse> result = performanceService.getBoxOfficeRank(request, pageRequest);

        //then
        List<BoxOfficeRankResponse> contents = result.getContents();
        assertThat(contents).hasSize(3);
        assertThat(contents).extracting("id")
            .containsExactly(performance2.getId(), performance1.getId(), performance3.getId());
        assertThat(contents).extracting("rank")
            .containsExactly(1, 2, 3);
        assertThat(contents.get(0).getStoryUrls()).hasSize(0);
    }

    @Test
    @DisplayName("BoxOffice rank 조회 - 순위 데이터가 없는 경우.")
    void getBoxOfficeRank_noPerformanceIds() {
        //given
        Performance performance2 = createPerformance("2");
        Performance performance1 = createPerformance("1");
        Performance performance3 = createPerformance("3");
        Performance performance4 = createPerformance("4");

        performanceRepository.saveAll(List.of(performance2, performance1, performance3, performance4));

        BoxOfficeRank boxOfficeRank = BoxOfficeRank.builder()
            .timePeriod(TimePeriod.DAY)
            .boxOfficeArea(BoxOfficeArea.BUSAN)
            .boxOfficeGenre(BoxOfficeGenre.MIXED_GENRE)
            .build();

        boxOfficeRankRepository.save(boxOfficeRank);

        //when
        BoxOfficeRankRequest request = BoxOfficeRankRequest.builder()
            .timePeriod(TimePeriod.DAY)
            .boxOfficeArea(BoxOfficeArea.BUSAN)
            .boxOfficeGenre(BoxOfficeGenre.MIXED_GENRE)
            .build();

        CustomPageRequest customPageRequest = new CustomPageRequest(1, 3);
        PageRequest pageRequest = customPageRequest.of();

        PageResponse<BoxOfficeRankResponse> result = performanceService.getBoxOfficeRank(request, pageRequest);

        //then
        List<BoxOfficeRankResponse> contents = result.getContents();
        assertThat(contents).hasSize(0);
    }

    @Test
    @DisplayName("Performance recommendation 조회")
    void getPerformanceRecommendation() {
        //given
        Genre genre = Genre.CLASSIC;
        Area area = Area.SEJONG;

        Performance performance = Performance.builder()
            .id("id")
            .title("title")
            .genre(genre)
            .area(area)
            .views(0)
            .build();

        Performance performance1 = createPerformanceWithGenre("genre1", genre);
        Performance performance2 = createPerformanceWithGenre("genre2", genre);
        Performance performance3 = createPerformanceWithArea("area1", area);
        Performance performance4 = createPerformanceWithArea("area2", area);

        BoxOfficeRank boxOfficeRankGenre = BoxOfficeRank.builder()
            .boxOfficeGenre(genre.getBoxOfficeGenre())
            .timePeriod(TimePeriod.WEEK)
            .performanceIds(performance1.getId() + "|" + performance2.getId())
            .build();

        BoxOfficeRank boxOfficeRankArea = BoxOfficeRank.builder()
            .boxOfficeArea(area.getBoxOfficeArea())
            .timePeriod(TimePeriod.WEEK)
            .performanceIds(performance3.getId() + "|" + performance4.getId())
            .build();

        performanceRepository.saveAll(List.of(performance, performance1, performance2, performance3, performance4));
        boxOfficeRankRepository.saveAll(List.of(boxOfficeRankGenre, boxOfficeRankArea));

        //when
        List<PerformanceResponse> result = performanceService.getPerformanceRecommendation(performance.getId());

        //then
        assertThat(result).hasSize(4);
        assertThat(result).extracting("id")
            .containsExactly(performance1.getId(), performance2.getId(), performance3.getId(), performance4.getId());
    }

    @Test
    @DisplayName("Performance genre 개수 조회 - 장르별 개수순으로 내림차순 정렬")
    void getPerformanceGenreCount() {
        //given
        Performance performance1 = createPerformanceWithGenre("id1", Genre.MIXED_GENRE);
        Performance performance2 = createPerformanceWithGenre("id2", Genre.MIXED_GENRE);
        Performance performance3 = createPerformanceWithGenre("id3", Genre.CIRCUS_AND_MAGIC);
        Performance performance4 = createPerformanceWithGenre("id4", Genre.CIRCUS_AND_MAGIC);
        Performance performance5 = createPerformanceWithGenre("id5", Genre.CLASSIC);
        Performance performance6 = createPerformanceWithGenre("id6", Genre.CLASSIC);
        Performance performance7 = createPerformanceWithGenre("id7", Genre.CLASSIC);
        Performance performance8 = createPerformanceWithGenre("id8", Genre.MUSICAL);

        performanceRepository.saveAll(List.of(performance1, performance2, performance3, performance4,
            performance5, performance6, performance7, performance8));

        //when
        List<GenreCountResponse> result = performanceService.getPerformanceGenreCount();

        //then
        assertThat(result).hasSize(Genre.values().length - 1); // ALL인 경우는 제외
        assertThat(result.get(0).getGenre()).isEqualTo(Genre.CLASSIC);
        assertThat(result.get(0).getCount()).isEqualTo(3L);
        assertThat(result.get(1).getGenre()).isEqualTo(Genre.CIRCUS_AND_MAGIC);
        assertThat(result.get(1).getCount()).isEqualTo(2L);
        assertThat(result.get(2).getGenre()).isEqualTo(Genre.MIXED_GENRE);
        assertThat(result.get(2).getCount()).isEqualTo(2L);
        assertThat(result.get(3).getGenre()).isEqualTo(Genre.MUSICAL);
        assertThat(result.get(3).getCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Performance filter 조회 - 장르, 가격 조회")
    void getPerformancesByCondition_withGenre() {
        //given
        Performance performance1 = createPerformanceWithGenre("performance1", Genre.CLASSIC);
        Performance performance2 = createPerformanceWithGenre("performance2", Genre.MUSICAL);
        Performance performance3 = createPerformanceWithGenre("performance3", Genre.MUSICAL);
        Performance performance4 = createPerformanceWithGenre("performance4", Genre.KOREAN_TRADITIONAL_MUSIC);

        performanceRepository.saveAll(List.of(performance1, performance2, performance3, performance4));

        PerformancePrice price1 = createPerformancePrice(performance1, 10000);
        PerformancePrice price2 = createPerformancePrice(performance1, 30000);
        PerformancePrice price3 = createPerformancePrice(performance2, 40000);
        PerformancePrice price4 = createPerformancePrice(performance2, 70000);
        PerformancePrice price5 = createPerformancePrice(performance3, 20000);
        PerformancePrice price6 = createPerformancePrice(performance3, 90000);
        PerformancePrice price7 = createPerformancePrice(performance4, 110000);

        performancePriceRepository.saveAll(List.of(price1, price2, price3, price4, price5, price6, price7));
        //when
        PerformanceFilterRequest request = PerformanceFilterRequest.builder()
            .genres(List.of(Genre.MUSICAL, Genre.KOREAN_TRADITIONAL_MUSIC))
            .minPrice(10000)
            .maxPrice(40000)
            .build();

        CustomPageRequest customPageRequest = new CustomPageRequest(1, 10);
        PageRequest pageRequest = customPageRequest.of();

        PageResponse<PerformanceResponse> result = performanceService.getPerformancesByCondition(request, pageRequest);

        //then
        List<PerformanceResponse> contents = result.getContents();
        assertThat(contents).hasSize(2);
        assertThat(contents).extracting("id").containsOnly(performance2.getId(), performance3.getId());
    }

    @Test
    @DisplayName("Performance filter 조회 - 지역, 가격 조회")
    void getPerformancesByCondition_withArea() {
        //given
        Performance performance1 = createPerformanceWithArea("performance1", Area.BUSAN);
        Performance performance2 = createPerformanceWithArea("performance2", Area.BUSAN);
        Performance performance3 = createPerformanceWithArea("performance3", Area.CHUNGBUK);
        Performance performance4 = createPerformanceWithArea("performance4", Area.JEJU);

        performanceRepository.saveAll(List.of(performance1, performance2, performance3, performance4));

        PerformancePrice price1 = createPerformancePrice(performance1, 10000);
        PerformancePrice price2 = createPerformancePrice(performance1, 30000);
        PerformancePrice price3 = createPerformancePrice(performance2, 40000);
        PerformancePrice price4 = createPerformancePrice(performance2, 70000);
        PerformancePrice price5 = createPerformancePrice(performance3, 20000);
        PerformancePrice price6 = createPerformancePrice(performance3, 90000);
        PerformancePrice price7 = createPerformancePrice(performance4, 110000);

        performancePriceRepository.saveAll(List.of(price1, price2, price3, price4, price5, price6, price7));

        //when
        PerformanceFilterRequest request = PerformanceFilterRequest.builder()
            .areas(List.of(Area.CHUNGBUK, Area.JEJU))
            .minPrice(10000)
            .build();

        CustomPageRequest customPageRequest = new CustomPageRequest(1, 10);
        PageRequest pageRequest = customPageRequest.of();

        PageResponse<PerformanceResponse> result = performanceService.getPerformancesByCondition(request, pageRequest);

        //then
        List<PerformanceResponse> contents = result.getContents();
        assertThat(contents).hasSize(1);
        assertThat(contents).extracting("id").containsOnly(performance4.getId());
    }

    @Test
    @DisplayName("Performance filter 조회 - 상태 조회, 시간 순 조회")
    void getPerformancesByCondition_withStatuses() {
        //given
        Performance performance1 = createPerformanceWithStatus("performance1", PerformanceStatus.ONGOING);
        performanceRepository.save(performance1);
        Performance performance2 = createPerformanceWithStatus("performance2", PerformanceStatus.COMPLETED);
        performanceRepository.save(performance2);
        Performance performance3 = createPerformanceWithStatus("performance3", PerformanceStatus.ONGOING);
        performanceRepository.save(performance3);
        Performance performance4 = createPerformanceWithStatus("performance4", PerformanceStatus.UPCOMING);
        performanceRepository.save(performance4);

        //when
        PerformanceFilterRequest request = PerformanceFilterRequest.builder()
            .statuses(List.of(PerformanceStatus.ONGOING, PerformanceStatus.UPCOMING))
            .sortType(SortType.TIME)
            .sortOrder(SortOrder.DESC)
            .build();

        CustomPageRequest customPageRequest = new CustomPageRequest(1, 10);
        PageRequest pageRequest = customPageRequest.of();

        PageResponse<PerformanceResponse> result = performanceService.getPerformancesByCondition(request, pageRequest);

        //then
        List<PerformanceResponse> contents = result.getContents();
        assertThat(contents).hasSize(3);
        assertThat(contents).extracting("id").containsExactly(performance4.getId(), performance3.getId(), performance1.getId());
    }

    @Test
    @DisplayName("Performance filter 조회 - 전체 조회")
    void getPerformancesByCondition_withNoFilter() {
        //given
        Performance performance1 = createPerformanceWithArea("performance1", Area.BUSAN);
        Performance performance2 = createPerformanceWithArea("performance2", Area.BUSAN);
        Performance performance3 = createPerformanceWithArea("performance3", Area.CHUNGBUK);
        Performance performance4 = createPerformanceWithArea("performance4", Area.JEJU);

        performanceRepository.saveAll(List.of(performance1, performance2, performance3, performance4));

        //when
        PerformanceFilterRequest request = PerformanceFilterRequest.builder()
            .build();

        CustomPageRequest customPageRequest = new CustomPageRequest(1, 10);
        PageRequest pageRequest = customPageRequest.of();

        PageResponse<PerformanceResponse> result = performanceService.getPerformancesByCondition(request, pageRequest);

        //then
        List<PerformanceResponse> contents = result.getContents();
        assertThat(contents).hasSize(4);
    }

    @Test
    @DisplayName("Performance 검색")
    void searchPerformance() {
        //given
        Performance performance = Performance.builder()
            .id("id")
            .title("브루노 마스 내한 공연")
            .views(0)
            .status(PerformanceStatus.ONGOING)
            .build();

        performanceRepository.save(performance);

        //when
        PerformanceSearchRequest request1 = PerformanceSearchRequest.builder()
            .searchType(SearchType.ARTIST)
            .query("브루노")
            .build();

        PerformanceSearchRequest request2 = PerformanceSearchRequest.builder()
            .searchType(SearchType.PERFORMANCE)
            .query("부루노")
            .build();

        PageResponse<PerformanceResponse> performanceResponsePageResponse1 = performanceService.searchPerformances(request1, PageRequest.of(0, 10));
        PageResponse<PerformanceResponse> performanceResponsePageResponse2 = performanceService.searchPerformances(request2, PageRequest.of(0, 10));

        //then
        assertThat(performanceResponsePageResponse1.getContents()).hasSize(1);
        assertThat(performanceResponsePageResponse1.getContents().get(0).getId()).isEqualTo(performance.getId());
        assertThat(performanceResponsePageResponse2.getContents()).hasSize(0);
    }

    private Performance createPerformance(String id) {
        return Performance.builder()
            .id(id)
            .title("title")
            .views(0)
            .status(PerformanceStatus.ONGOING)
            .build();
    }

    private Performance createPerformanceWithGenre(String id, Genre genre) {
        return Performance.builder()
            .id(id)
            .title("title")
            .genre(genre)
            .views(0)
            .build();
    }

    private Performance createPerformanceWithArea(String id, Area area) {
        return Performance.builder()
            .id(id)
            .title("title")
            .area(area)
            .views(0)
            .build();
    }

    private Performance createPerformanceWithStatus(String id, PerformanceStatus status) {
        return Performance.builder()
            .id(id)
            .title("title")
            .status(status)
            .area(Area.CHUNGBUK)
            .views(0)
            .build();
    }

    private Place createPlace(String id) {
        return Place.builder()
            .id(id)
            .address("address")
            .build();
    }

    private PerformancePrice createPerformancePrice(Performance performance, int price) {
        return PerformancePrice.builder()
            .performance(performance)
            .price(price)
            .build();
    }
}
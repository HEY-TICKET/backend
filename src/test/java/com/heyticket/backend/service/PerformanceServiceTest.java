package com.heyticket.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import com.heyticket.backend.config.JpaConfig;
import com.heyticket.backend.domain.BoxOfficeRank;
import com.heyticket.backend.domain.Performance;
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
import com.heyticket.backend.service.dto.response.BoxOfficeRankResponse;
import com.heyticket.backend.service.dto.response.PerformanceResponse;
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
    private PlaceService placeService;

    @Mock
    private KopisService kopisService;

    @BeforeEach
    void init() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("email", "password");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        performanceService = new PerformanceService(performanceRepository, performancePriceRepository, boxOfficeRankRepository, placeRepository, placeService, kopisService);
    }

    @AfterEach
    void deleteAll() {
        performanceRepository.deleteAll();
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
        assertThat(result.getPageSize()).isEqualTo(10);
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
        assertThat(result.getPageSize()).isEqualTo(10);
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
        assertThat(result.getPageSize()).isEqualTo(10);
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
        assertThat(result.getPageSize()).isEqualTo(10);
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
            .area(BoxOfficeArea.BUSAN)
            .genre(BoxOfficeGenre.MIXED_GENRE)
            .build();

        boxOfficeRankRepository.save(boxOfficeRank);

        //when
        BoxOfficeRankRequest request = BoxOfficeRankRequest.builder()
            .timePeriod(TimePeriod.DAY)
            .area(BoxOfficeArea.BUSAN)
            .genre(BoxOfficeGenre.MIXED_GENRE)
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
            .area(BoxOfficeArea.BUSAN)
            .genre(BoxOfficeGenre.MIXED_GENRE)
            .build();

        boxOfficeRankRepository.save(boxOfficeRank);

        //when
        BoxOfficeRankRequest request = BoxOfficeRankRequest.builder()
            .timePeriod(TimePeriod.DAY)
            .area(BoxOfficeArea.BUSAN)
            .genre(BoxOfficeGenre.MIXED_GENRE)
            .build();

        CustomPageRequest customPageRequest = new CustomPageRequest(1, 3);
        PageRequest pageRequest = customPageRequest.of();

        PageResponse<BoxOfficeRankResponse> result = performanceService.getBoxOfficeRank(request, pageRequest);

        //then
        List<BoxOfficeRankResponse> contents = result.getContents();
        assertThat(contents).hasSize(0);
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

    private Place createPlace(String id) {
        return Place.builder()
            .id(id)
            .address("address")
            .build();
    }
}
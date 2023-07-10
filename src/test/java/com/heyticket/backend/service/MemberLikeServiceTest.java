package com.heyticket.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.heyticket.backend.config.JpaConfig;
import com.heyticket.backend.domain.Member;
import com.heyticket.backend.domain.MemberLike;
import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.domain.Place;
import com.heyticket.backend.repository.member.MemberLikeRepository;
import com.heyticket.backend.repository.member.MemberRepository;
import com.heyticket.backend.repository.performance.PerformanceRepository;
import com.heyticket.backend.repository.place.PlaceRepository;
import com.heyticket.backend.service.dto.pagable.PageResponse;
import com.heyticket.backend.service.dto.request.MemberLikeListRequest;
import com.heyticket.backend.service.dto.request.MemberLikeSaveRequest;
import com.heyticket.backend.service.dto.response.PerformanceResponse;
import com.heyticket.backend.service.enums.Area;
import com.heyticket.backend.service.enums.Genre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(JpaConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
class MemberLikeServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberLikeRepository memberLikeRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @PersistenceContext
    private EntityManager em;

    private MemberLikeService memberLikeService;

    @BeforeEach
    void init() {
        memberLikeService = new MemberLikeService(memberLikeRepository, memberRepository, performanceRepository);
    }

    @Test
    @DisplayName("MemberLike 등록 - 데이터 확인")
    void hitLike() {
        //given
        Member member = createMember("email");
        memberRepository.save(member);

        Performance performance = createPerformance("performanceId");
        performanceRepository.save(performance);

        //when
        MemberLikeSaveRequest request = MemberLikeSaveRequest.builder()
            .email(member.getEmail())
            .performanceId(performance.getId())
            .build();

        memberLikeService.hitLike(request);

        em.flush();
        em.clear();

        //then
        Member foundMember = memberRepository.findByEmail(member.getEmail())
            .orElseThrow(() -> new NoSuchElementException("No such member."));
        List<MemberLike> memberLikes = foundMember.getMemberLikes();
        assertThat(memberLikes).hasSize(1);
        assertThat(memberLikes.get(0).getPerformance().getId()).isEqualTo(performance.getId());
    }

    @Test
    @DisplayName("MemberLike 등록 - 이미 찜한 공연일 경우 저장하지 않는다")
    void hitLike_alreadyHit() {
        //given
        Member member = createMember("email");
        memberRepository.save(member);

        Performance performance = createPerformance("performanceId");
        performanceRepository.save(performance);

        MemberLike memberLike = MemberLike.builder()
            .member(member)
            .performance(performance)
            .build();

        memberLikeRepository.save(memberLike);

        //when
        MemberLikeSaveRequest request = MemberLikeSaveRequest.builder()
            .email(member.getEmail())
            .performanceId(performance.getId())
            .build();

        memberLikeService.hitLike(request);

        em.flush();
        em.clear();

        //then
        Member foundMember = memberRepository.findByEmail(member.getEmail())
            .orElseThrow(() -> new NoSuchElementException("No such member."));
        List<MemberLike> memberLikes = foundMember.getMemberLikes();
        assertThat(memberLikes).hasSize(1);
        assertThat(memberLikes.get(0).getPerformance().getId()).isEqualTo(performance.getId());
    }

    @Test
    @DisplayName("MemberLike 등록 취소 - 데이터 확인")
    void cancelLike() {
        //given
        Member member = createMember("email");
        memberRepository.save(member);

        Performance performance = createPerformance("performanceId");
        performanceRepository.save(performance);

        MemberLike memberLike = MemberLike.builder()
            .performance(performance)
            .member(member)
            .build();

        memberLikeRepository.save(memberLike);

        //when
        MemberLikeSaveRequest request = MemberLikeSaveRequest.builder()
            .email(member.getEmail())
            .performanceId(performance.getId())
            .build();

        memberLikeService.cancelLike(request);

        em.flush();
        em.clear();

        //then
        List<MemberLike> memberLikes = memberLikeRepository.findAll();
        assertThat(memberLikes).hasSize(0);
    }

    @Test
    @DisplayName("MemberLike 등록 취소 - 찜한 이력이 없는 경우 요청을 종료한다")
    void cancelLike_noMemberLike() {
        //given
        Member member = createMember("email");
        memberRepository.save(member);

        Performance performance = createPerformance("performanceId");
        performanceRepository.save(performance);

        //when
        MemberLikeSaveRequest request = MemberLikeSaveRequest.builder()
            .email(member.getEmail())
            .performanceId(performance.getId())
            .build();

        memberLikeService.cancelLike(request);

        em.flush();
        em.clear();

        //then
        List<MemberLike> memberLikes = memberLikeRepository.findAll();
        assertThat(memberLikes).hasSize(0);
    }

    @Test
    @DisplayName("MemberLike 조회 - 데이터 확인")
    void getMemberLikedPerformances() {
        //given
        Place place = Place.builder()
            .id("placeId")
            .area(Area.BUSAN)
            .address("address")
            .phoneNumber("phoneNumber")
            .gugunName("gugunName")
            .build();

        Place savedPlace = placeRepository.save(place);

        Member member = createMember("email");
        memberRepository.save(member);

        Performance performance1 = Performance.builder()
            .id("performance1")
            .title("title")
            .genre(Genre.MUSICAL)
            .area(Area.BUSAN)
            .theater("theater")
            .place(savedPlace)
            .build();

        Performance performance2 = Performance.builder()
            .id("performance2")
            .title("title")
            .genre(Genre.MUSICAL)
            .area(Area.BUSAN)
            .theater("theater")
            .place(savedPlace)
            .build();

        performanceRepository.saveAll(List.of(performance1, performance2));

        MemberLike memberLike1 = MemberLike.builder()
            .performance(performance1)
            .member(member)
            .build();

        MemberLike memberLike2 = MemberLike.builder()
            .performance(performance2)
            .member(member)
            .build();

        memberLikeRepository.saveAll(List.of(memberLike1, memberLike2));

        //when
        MemberLikeListRequest request = MemberLikeListRequest.builder()
            .email(member.getEmail())
            .build();

        PageRequest pageRequest = PageRequest.of(0, 10);
        PageResponse<PerformanceResponse> memberLikedPerformances = memberLikeService.getMemberLikedPerformances(request, pageRequest);

        em.flush();
        em.clear();

        //then
        List<PerformanceResponse> contents = memberLikedPerformances.getContents();
        assertThat(contents).hasSize(2);
        assertThat(contents).extracting("id").containsOnly(performance1.getId(), performance2.getId());
    }

    private Member createMember(String email) {
        return Member.builder()
            .email(email)
            .password("encodedPassword")
            .build();
    }

    private Performance createPerformance(String id) {
        return Performance.builder()
            .id(id)
            .title("title")
            .genre(Genre.MUSICAL)
            .area(Area.BUSAN)
            .theater("theater")
            .build();
    }
}
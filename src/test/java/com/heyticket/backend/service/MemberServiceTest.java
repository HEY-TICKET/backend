package com.heyticket.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.heyticket.backend.domain.Member;
import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.module.kopis.enums.Area;
import com.heyticket.backend.module.kopis.enums.Genre;
import com.heyticket.backend.repository.MemberGenreRepository;
import com.heyticket.backend.repository.MemberKeywordRepository;
import com.heyticket.backend.repository.MemberLikeRepository;
import com.heyticket.backend.repository.MemberRepository;
import com.heyticket.backend.repository.PerformanceRepository;
import com.heyticket.backend.service.dto.VerificationCode;
import com.heyticket.backend.service.dto.request.MemberSignUpRequest;
import com.heyticket.backend.service.dto.response.MemberResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberLikeRepository memberLikeRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private MemberGenreRepository memberGenreRepository;

    @Autowired
    private MemberKeywordRepository memberKeywordRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CacheService cacheService;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void init() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("email", "password");
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void deleteAll() {
        memberGenreRepository.deleteAll();
        memberLikeRepository.deleteAll();
        memberKeywordRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("Member 조회")
    void getMemberByEmail() {
        //given
        Member member = createMember("email");
        memberRepository.save(member);

        //when
        MemberResponse memberResponse = memberService.getMemberByEmail(member.getEmail());

        //then
        assertThat(memberResponse.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("Member 가입 - 정상 가입 데이터 확인")
    @Transactional
    void signUp() {
        //given
        MemberSignUpRequest request = MemberSignUpRequest.builder()
            .email("email")
            .password("Password123")
            .areas(List.of(Area.GYEONGGI, Area.SEOUL))
            .genres(List.of(Genre.MUSICAL, Genre.THEATER))
            .keywords(List.of("맘마미아"))
            .verificationCode("verificationCode")
            .build();

        cacheService.putVerificationCode("email", VerificationCode.of("verificationCode"));

        //when
        memberService.signUp(request);

        entityManager.flush();
        entityManager.clear();

        //then
        Member foundMember = memberRepository.findById(request.getEmail())
            .orElseThrow(() -> new NoSuchElementException("No such user."));

        assertThat(foundMember.getEmail()).isEqualTo(request.getEmail());
        assertThat(foundMember.getMemberGenres()).extracting("genre").containsOnly(Genre.MUSICAL, Genre.THEATER);
        assertThat(foundMember.getMemberAreas()).extracting("area").containsOnly(Area.GYEONGGI, Area.SEOUL);
        assertThat(foundMember.getMemberKeywords().get(0).getKeyword()).isEqualTo(request.getKeywords().get(0));
    }

    @Test
    @DisplayName("Member 가입 - 비밀번호 양식 오류")
    @Transactional
    void signUp_passwordValidationFailure() {
        //given
        MemberSignUpRequest request = MemberSignUpRequest.builder()
            .email("email")
            .password("password123")
            .areas(List.of(Area.GYEONGGI, Area.SEOUL))
            .genres(List.of(Genre.MUSICAL, Genre.THEATER))
            .keywords(List.of("맘마미아"))
            .verificationCode("verificationCode")
            .build();

        cacheService.putVerificationCode("email", VerificationCode.of("verificationCode"));

        //when
        Throwable throwable = catchThrowable(() -> memberService.signUp(request));

        entityManager.flush();
        entityManager.clear();

        //then
        assertThat(throwable).isInstanceOf(IllegalStateException.class);
    }


    private Member createMember(String email) {
        return Member.builder()
            .email(email)
            .password("password")
            .memberAreas(new ArrayList<>())
            .memberGenres(new ArrayList<>())
            .memberLikes(new ArrayList<>())
            .memberKeywords(new ArrayList<>())
            .build();
    }

    private Performance createPerformance(String id) {
        return Performance.builder()
            .id(id)
            .title("title")
            .build();
    }
}
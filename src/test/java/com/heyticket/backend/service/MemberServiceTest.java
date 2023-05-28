package com.heyticket.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.heyticket.backend.domain.Member;
import com.heyticket.backend.domain.MemberGenre;
import com.heyticket.backend.domain.MemberKeyword;
import com.heyticket.backend.domain.MemberLike;
import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.module.kopis.enums.Genre;
import com.heyticket.backend.repository.MemberGenreRepository;
import com.heyticket.backend.repository.MemberKeywordRepository;
import com.heyticket.backend.repository.MemberLikeRepository;
import com.heyticket.backend.repository.MemberRepository;
import com.heyticket.backend.repository.PerformanceRepository;
import com.heyticket.backend.service.dto.request.MemberCategoryUpdateRequest;
import com.heyticket.backend.service.dto.request.MemberKeywordUpdateRequest;
import com.heyticket.backend.service.dto.request.MemberLikeRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

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
    void deleteMember() {
        List<Member> all = memberRepository.findAll();
        for (Member member : all) {
            List<MemberGenre> memberGenres = member.getMemberGenres();
            memberRepository.deleteById(member.getEmail());
        }
        assertThat(memberRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    void saveMember() {
        Member member = Member.builder()
            .email("email")
            .password("pw")
            .build();

        memberRepository.save(member);

        MemberGenre memberGenre = MemberGenre.builder()
            .genre(Genre.MIXED_GENRE)
            .member(member)
            .build();

        memberGenreRepository.save(memberGenre);

        Optional<Member> byEmail = memberRepository.findByEmail(member.getEmail());
        assertThat(byEmail).isPresent();
    }

    @Test
    void cacheService() {
        Cache<String, String> cache = CacheBuilder.newBuilder()
            .build();
        cache.put("1", "a");

        long size0 = cache.size();
        System.out.println("size0 = " + size0);

        cache.invalidate("1");
        long size1 = cache.size();
        System.out.println("size1 = " + size1);

        cache.invalidate("2");
        long size2 = cache.size();
        System.out.println("size2 = " + size2);
    }

    @Test
    void likePerformance() {
        Member member = Member.builder()
            .email("email")
            .password("password")
            .memberAreas(new ArrayList<>())
            .memberGenres(new ArrayList<>())
            .memberLikes(new ArrayList<>())
            .memberKeywords(new ArrayList<>())
            .build();

        Performance performance = Performance.builder()
            .id("id")
            .title("title")
            .build();

        memberRepository.save(member);
        performanceRepository.save(performance);

        memberService.likePerformance(performance.getId());

        List<MemberLike> all = memberLikeRepository.findAll();
        assertThat(all).hasSize(1);
    }

    @Test
    void updatePreferredCategory() {
        Member member = createMember("email");
        MemberGenre memberGenre1 = MemberGenre.builder()
            .genre(Genre.DANCE)
            .member(member)
            .build();

        MemberGenre memberGenre2 = MemberGenre.builder()
            .genre(Genre.MIXED_GENRE)
            .member(member)
            .build();

        MemberGenre memberGenre3 = MemberGenre.builder()
            .genre(Genre.CLASSIC)
            .member(member)
            .build();

        member.getMemberGenres().addAll(List.of(memberGenre1, memberGenre2, memberGenre3));

        memberRepository.save(member);

        MemberCategoryUpdateRequest request = MemberCategoryUpdateRequest.builder()
            .email(member.getEmail())
            .genres(List.of("무용(서양/한국무용)", "뮤지컬"))
            .build();

        memberService.updatePreferredCategory(request);

        List<MemberGenre> all = memberGenreRepository.findAll();
        for (MemberGenre memberGenre : all) {
            System.out.println("memberGenre.getGenre() = " + memberGenre.getGenre());
        }
    }

    @Test
    void updateKeyword() {
        Member member = createMember("email");
        MemberKeyword keyword1 = MemberKeyword.builder()
            .keyword("keyword1")
            .member(member)
            .build();

        MemberKeyword keyword2 = MemberKeyword.builder()
            .keyword("keyword2")
            .member(member)
            .build();

        member.getMemberKeywords().addAll(List.of(keyword1, keyword2));

        memberRepository.save(member);

        MemberKeywordUpdateRequest request = MemberKeywordUpdateRequest.builder()
            .email(member.getEmail())
            .keywords(List.of("keyword2", "keyword3"))
            .build();

        memberService.updatePreferredKeyword(request);

        List<MemberKeyword> keywords = memberKeywordRepository.findAll();
        assertThat(keywords).extracting("keyword").containsOnly("keyword2", "keyword3");
    }

    @Test
    void hitLike() {
        Member email = createMember("email");
        memberRepository.save(email);

        Performance perf = createPerformance("id");
        performanceRepository.save(perf);

        MemberLikeRequest request = MemberLikeRequest.builder()
            .performanceId(perf.getId())
            .email(email.getEmail())
            .build();

        memberService.hitLike(request);

        List<MemberLike> all = memberLikeRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getMember().getEmail()).isEqualTo(email.getEmail());
        assertThat(all.get(0).getPerformance().getId()).isEqualTo(perf.getId());
    }

    @Test
    void cancelLike() {
        Member email = createMember("email");
        memberRepository.save(email);

        Performance perf = createPerformance("id");
        performanceRepository.save(perf);

        MemberLikeRequest request = MemberLikeRequest.builder()
            .performanceId(perf.getId())
            .email(email.getEmail())
            .build();

        memberService.cancelLike(request);

        List<MemberLike> all = memberLikeRepository.findAll();
        assertThat(all).hasSize(0);
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
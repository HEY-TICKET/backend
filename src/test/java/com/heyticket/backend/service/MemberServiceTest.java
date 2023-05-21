//package com.heyticket.backend.service;
//
//import com.google.common.cache.Cache;
//import com.google.common.cache.CacheBuilder;
//import com.heyticket.backend.domain.Member;
//import com.heyticket.backend.domain.MemberGenre;
//import com.heyticket.backend.module.kopis.enums.Genre;
//import com.heyticket.backend.repository.MemberGenreRepository;
//import com.heyticket.backend.repository.MemberRepository;
//import java.util.List;
//import java.util.Optional;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//class MemberServiceTest {
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private MemberGenreRepository memberGenreRepository;
//
//    @Test
//    void deleteMember() {
//        List<Member> all = memberRepository.findAll();
//        for (Member member : all) {
//            List<MemberGenre> memberGenres = member.getMemberGenres();
//            memberRepository.deleteById(member.getEmail());
//        }
//        Assertions.assertThat(memberRepository.findAll().size()).isEqualTo(0);
//    }
//
//    @Test
////    @Transactional
//    void saveMember() {
//        Member member = Member.builder()
//            .email("email")
//            .password("pw")
//            .build();
//
//        MemberGenre memberGenre = MemberGenre.builder()
//            .genre(Genre.MIXED_GENRE)
//            .member(member)
//            .build();
//
//        memberGenreRepository.save(memberGenre);
//
//        Optional<Member> byEmail = memberRepository.findByEmail(member.getEmail());
//        Assertions.assertThat(byEmail).isPresent();
//
//    }
//
//    @Test
//    void cacheService() {
//        Cache<String, String> cache = CacheBuilder.newBuilder()
//            .build();
//        cache.put("1", "a");
//
//        long size0 = cache.size();
//        System.out.println("size0 = " + size0);
//
//        cache.invalidate("1");
//        long size1 = cache.size();
//        System.out.println("size1 = " + size1);
//
//        cache.invalidate("2");
//        long size2 = cache.size();
//        System.out.println("size2 = " + size2);
//    }
//
//}
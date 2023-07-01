package com.heyticket.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.heyticket.backend.config.JpaConfig;
import com.heyticket.backend.domain.Member;
import com.heyticket.backend.domain.MemberLike;
import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.repository.MemberLikeRepository;
import com.heyticket.backend.repository.MemberRepository;
import com.heyticket.backend.repository.PerformanceRepository;
import com.heyticket.backend.service.dto.request.MemberLikeSaveRequest;
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

    @PersistenceContext
    private EntityManager em;

    private MemberLikeService memberLikeService;

    @BeforeEach
    void init() {
        memberLikeService = new MemberLikeService(memberLikeRepository, memberRepository, performanceRepository);
    }

    @Test
    @DisplayName("MemberLike 등록 - 데이터 확인")
    void hitLike(){
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
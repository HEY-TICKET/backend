package com.heyticket.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.heyticket.backend.config.JpaConfig;
import com.heyticket.backend.domain.Keyword;
import com.heyticket.backend.domain.Member;
import com.heyticket.backend.domain.MemberKeyword;
import com.heyticket.backend.exception.ValidationFailureException;
import com.heyticket.backend.repository.keyword.KeywordRepository;
import com.heyticket.backend.repository.member.MemberKeywordRepository;
import com.heyticket.backend.repository.member.MemberRepository;
import com.heyticket.backend.service.dto.request.KeywordDeleteRequest;
import com.heyticket.backend.service.dto.request.KeywordSaveRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(JpaConfig.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class KeywordServiceTest {

    private KeywordService keywordService;

    @Autowired
    private MemberKeywordRepository memberKeywordRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Mock
    private FcmService fcmService;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        keywordService = new KeywordService(fcmService, keywordRepository, memberKeywordRepository, memberRepository);
    }

    @AfterEach
    void deleteAll() {
        memberKeywordRepository.deleteAll();
        keywordRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("Keyword 추가 - 새로운 키워드일 경우 키워드를 추가하고 연관관계를 맺는다")
    void saveKeyword_newKeyword() {
        //given
        Member member = createMember("email");

        memberRepository.save(member);

        //when
        KeywordSaveRequest request = KeywordSaveRequest.builder()
            .email(member.getEmail())
            .keyword("keyword")
            .build();

        keywordService.saveKeyword(request);

        //then
        List<MemberKeyword> memberKeywords = memberKeywordRepository.findAll();
        assertThat(memberKeywords).hasSize(1);
        MemberKeyword foundMemberKeyword = memberKeywords.get(0);
        assertThat(foundMemberKeyword.getMember().getEmail()).isEqualTo(member.getEmail());
        assertThat(foundMemberKeyword.getKeyword().getContent()).isEqualTo(request.getKeyword());
    }

    @Test
    @DisplayName("Keyword 추가 - 기존에 등록된 키워드일 경우 연관관계를 맺는다")
    void saveKeyword_keywordExist() {
        //given
        String content = "content";

        Member member = createMember("email");

        memberRepository.save(member);

        Keyword keyword = Keyword.of(content);
        keywordRepository.save(keyword);

        //when
        KeywordSaveRequest request = KeywordSaveRequest.builder()
            .email(member.getEmail())
            .keyword(content)
            .build();

        keywordService.saveKeyword(request);

        //then
        List<MemberKeyword> memberKeywords = memberKeywordRepository.findAll();
        assertThat(memberKeywords).hasSize(1);
        MemberKeyword foundMemberKeyword = memberKeywords.get(0);
        assertThat(foundMemberKeyword.getMember().getEmail()).isEqualTo(member.getEmail());
        assertThat(foundMemberKeyword.getKeyword().getContent()).isEqualTo(request.getKeyword());
    }

    @Test
    @DisplayName("Keyword 추가 - 해당 Member가 이미 등록된 키워드일 경우 throw ValidationFailureException")
    void saveKeyword_duplicatedKeyword() {
        //given
        String content = "content";

        Member member = createMember("email");
        memberRepository.save(member);

        Keyword keyword = Keyword.of(content);
        keywordRepository.save(keyword);

        MemberKeyword memberKeyword = MemberKeyword.builder()
            .member(member)
            .keyword(keyword)
            .build();
        memberKeywordRepository.save(memberKeyword);

        entityManager.flush();
        entityManager.clear();

        //when
        KeywordSaveRequest request = KeywordSaveRequest.builder()
            .email(member.getEmail())
            .keyword(content)
            .build();

        Throwable throwable = catchThrowable(() -> keywordService.saveKeyword(request));

        //then
        assertThat(throwable).isInstanceOf(ValidationFailureException.class);
    }
    
    @Test
    @DisplayName("Keyword 삭제 - Keyword와 MemberKeyword를 삭제하고, keyword가 다른 Member와 연결되어 있으면 삭제하지 않는다")
    void deleteKeyword_keywordHasRef(){
        //given
        Member member1 = createMember("email1");
        Member member2 = createMember("email2");
        memberRepository.saveAll(List.of(member1, member2));

        Keyword keyword = Keyword.of("content");
        keywordRepository.save(keyword);

        MemberKeyword memberKeyword1 = MemberKeyword.builder()
            .member(member1)
            .keyword(keyword)
            .build();

        MemberKeyword memberKeyword2 = MemberKeyword.builder()
            .member(member2)
            .keyword(keyword)
            .build();
        memberKeywordRepository.saveAll(List.of(memberKeyword1, memberKeyword2));

        //when
        KeywordDeleteRequest request = KeywordDeleteRequest.builder()
            .email(member1.getEmail())
            .keyword(keyword.getContent())
            .build();

        keywordService.deleteKeyword(request);
        
        //then
        List<Keyword> foundKeywords = keywordRepository.findAll();
        assertThat(foundKeywords).hasSize(1);
        Keyword foundKeyword = foundKeywords.get(0);
        assertThat(foundKeyword.getContent()).isEqualTo(keyword.getContent());
        List<MemberKeyword> foundMemberKeywords = memberKeywordRepository.findAll();
        assertThat(foundMemberKeywords).hasSize(1);
        MemberKeyword foundMemberKeyword = foundMemberKeywords.get(0);
        assertThat(foundMemberKeyword.getMember().getEmail()).isEqualTo(member2.getEmail());
    }

    @Test
    @DisplayName("Keyword 삭제 - Keyword와 MemberKeyword를 삭제하고, keyword가 더이상 연결되어 있지 않으면 삭제한다")
    void deleteKeyword_keywordHasNoRef(){
        //given
        Member member = createMember("email");
        memberRepository.save(member);

        Keyword keyword = Keyword.of("content");
        keywordRepository.save(keyword);

        MemberKeyword memberKeyword = MemberKeyword.builder()
            .member(member)
            .keyword(keyword)
            .build();
        memberKeywordRepository.save(memberKeyword);

        entityManager.flush();
        entityManager.clear();

        //when
        KeywordDeleteRequest request = KeywordDeleteRequest.builder()
            .email(member.getEmail())
            .keyword(keyword.getContent())
            .build();

        keywordService.deleteKeyword(request);

        //then
        List<Keyword> foundKeywords = keywordRepository.findAll();
        assertThat(foundKeywords).isEmpty();
        List<MemberKeyword> foundMemberKeywords = memberKeywordRepository.findAll();
        assertThat(foundMemberKeywords).isEmpty();
    }

    private Member createMember(String email) {
        return Member.builder()
            .email(email)
            .password("encodedPassword")
            .memberAreas(new ArrayList<>())
            .memberGenres(new ArrayList<>())
            .memberLikes(new ArrayList<>())
            .memberKeywords(new ArrayList<>())
            .build();
    }
}

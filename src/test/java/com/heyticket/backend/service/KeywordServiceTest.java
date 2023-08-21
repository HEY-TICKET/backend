package com.heyticket.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.heyticket.backend.config.JpaConfig;
import com.heyticket.backend.domain.Keyword;
import com.heyticket.backend.domain.Member;
import com.heyticket.backend.domain.MemberKeyword;
import com.heyticket.backend.repository.keyword.KeywordRepository;
import com.heyticket.backend.repository.member.MemberKeywordRepository;
import com.heyticket.backend.repository.member.MemberRepository;
import com.heyticket.backend.service.dto.request.KeywordDeleteRequest;
import com.heyticket.backend.service.dto.request.KeywordSaveRequest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
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

    @BeforeEach
    void setUp() {
        keywordService = new KeywordService(keywordRepository, memberKeywordRepository, memberRepository);
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
        Member member = createMember();

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

        Member member = createMember();

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
    @DisplayName("Keyword 삭제 - keyword와 memberKeyword를 삭제한다")
    void deleteKeyword(){
        //given
        Member member = createMember();
        memberRepository.save(member);

        Keyword keyword = Keyword.of("content");
        keywordRepository.save(keyword);

        MemberKeyword memberKeyword = MemberKeyword.builder()
            .member(member)
            .keyword(keyword)
            .build();
        memberKeywordRepository.save(memberKeyword);

        //when
        KeywordDeleteRequest request = KeywordDeleteRequest.builder()
            .email(member.getEmail())
            .keyword(keyword.getContent())
            .build();

        keywordService.deleteKeyword(request);
        
        //then
        List<Keyword> foundKeywords = keywordRepository.findAll();
        assertThat(foundKeywords).hasSize(0);
        List<MemberKeyword> foundMemberKeywords = memberKeywordRepository.findAll();
        assertThat(foundMemberKeywords).hasSize(0);
    }

    private Member createMember() {
        return Member.builder()
            .email("email")
            .password("encodedPassword")
            .memberAreas(new ArrayList<>())
            .memberGenres(new ArrayList<>())
            .memberLikes(new ArrayList<>())
            .memberKeywords(new ArrayList<>())
            .build();
    }
}

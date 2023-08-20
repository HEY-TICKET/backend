package com.heyticket.backend.service;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.heyticket.backend.domain.Member;
import com.heyticket.backend.domain.MemberKeyword;
import com.heyticket.backend.repository.member.MemberKeywordRepository;
import com.heyticket.backend.repository.member.MemberRepository;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
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
        keywordService = new KeywordService(memberRepository, memberKeywordRepository, keywordRepository);
    }

    @AfterEach
    void deleteAll() {
        memberKeywordRepository.deleteAll();
        keywordRepository.deleteAll();
    }

    @Test
    @DisplayName("Keyword 추가 - 새로운 키워드일 경우 키워드를 추가하고 연관관계를 맺는다")
    void saveKeyword() {
        //given
        Member member = Member.builder()
            .email("email")
            .password("encodedPassword")
            .build();

        KeywordSaveRequest request = KeywordSaveRequest.builder()
            .email(member.getEmail())
            .keyword("keyword")
            .build();

        //when
        keywordService.saveKeyword(request);

        //then
        List<MemberKeyword> memberKeywords = memberKeywordRepository.findAll();
        assertThat(memberKeywords).hasSize(1);
        MemberKeyword foundMemberKeyword = memberKeywords.get(0);
        Member foundMember = foundMemberKeyword.getMember();
        assertThat(foundMember.getEmail()).isEqualTo(member.getEmail());
        Keyword foundKeyword = foundMemberKeyword.getKeyword();
        assertThat(foundKeyword.getContent()).isEqualTo(request.getKeyword());
    }
}

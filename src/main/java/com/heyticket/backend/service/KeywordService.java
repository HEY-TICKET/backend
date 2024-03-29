package com.heyticket.backend.service;

import com.heyticket.backend.domain.Keyword;
import com.heyticket.backend.domain.Member;
import com.heyticket.backend.domain.MemberKeyword;
import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.exception.NotFoundException;
import com.heyticket.backend.exception.ValidationFailureException;
import com.heyticket.backend.repository.keyword.KeywordRepository;
import com.heyticket.backend.repository.member.MemberKeywordRepository;
import com.heyticket.backend.repository.member.MemberRepository;
import com.heyticket.backend.service.dto.PushInfo;
import com.heyticket.backend.service.dto.request.KeywordDeleteRequest;
import com.heyticket.backend.service.dto.request.KeywordSaveRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KeywordService {

    private final IFcmService fcmService;

    private final KeywordRepository keywordRepository;

    private final MemberKeywordRepository memberKeywordRepository;

    private final MemberRepository memberRepository;

    public void saveKeyword(KeywordSaveRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new NotFoundException("No such member", InternalCode.NOT_FOUND));

        boolean isDuplicated = member.getMemberKeywords().stream()
            .anyMatch(memberKeyword -> memberKeyword.getKeyword().getContent().equals(request.getKeyword()));

        if (isDuplicated) {
            throw new ValidationFailureException("Duplicated keyword.", InternalCode.BAD_REQUEST);
        }

        Keyword keyword = getOrSave(request.getKeyword());

        MemberKeyword memberKeyword = MemberKeyword.builder()
            .member(member)
            .keyword(keyword)
            .build();

        memberKeywordRepository.save(memberKeyword);

        fcmService.subscribeTopic(member.getFcmToken(), request.getKeyword());
    }

    public void deleteKeyword(KeywordDeleteRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new NotFoundException("No such member", InternalCode.NOT_FOUND));

        Keyword keyword = keywordRepository.findByContent(request.getKeyword())
            .orElseThrow(() -> new NotFoundException("No such keyword", InternalCode.NOT_FOUND));

        memberKeywordRepository.deleteByMemberAndKeyword(member, keyword);

        if (keyword.getMemberKeywords().size() == 1) {
            keywordRepository.deleteById(keyword.getId());
        }

        fcmService.unsubscribeTopic(member.getFcmToken(), request.getKeyword());
    }

    public Keyword getOrSave(String content) {
        return keywordRepository.findByContent(content)
            .orElseGet(() -> {
                Keyword newKeyword = Keyword.of(content);
                return keywordRepository.save(newKeyword);
            });
    }

    public void sendKeywordPush(List<String> keywords, PushInfo pushInfo) {
        CompletableFuture.runAsync(() -> {
            for (String keyword : keywords) {
                fcmService.sendTopicMessage(keyword, pushInfo);
            }
        });
    }
}

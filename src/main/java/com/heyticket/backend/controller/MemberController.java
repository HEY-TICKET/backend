package com.heyticket.backend.controller;

import com.heyticket.backend.module.security.jwt.dto.TokenInfo;
import com.heyticket.backend.service.MemberService;
import com.heyticket.backend.service.dto.MemberLoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members/login")
    public TokenInfo login(@RequestBody MemberLoginRequest memberLoginRequest) {
        String memberId = memberLoginRequest.getMemberId();
        String password = memberLoginRequest.getPassword();
        TokenInfo tokenInfo = memberService.login(memberId, password);
        return tokenInfo;
    }
}

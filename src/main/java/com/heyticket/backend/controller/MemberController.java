package com.heyticket.backend.controller;

import com.heyticket.backend.module.security.jwt.dto.TokenInfo;
import com.heyticket.backend.service.EmailService;
import com.heyticket.backend.service.MemberService;
import com.heyticket.backend.service.dto.EmailVerificationRequest;
import com.heyticket.backend.service.dto.MemberLoginRequest;
import com.heyticket.backend.service.dto.MemberSignUpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    private final EmailService emailService;

    @PostMapping("/members/login")
    public TokenInfo login(@RequestBody MemberLoginRequest request) {
        return memberService.login(request);
    }

    @PostMapping("/members/signup")
    public void signUp(@RequestBody MemberSignUpRequest request) {
        memberService.signUp(request);
    }

    @PostMapping("/members/email/send")
    public void sendVerificationEmail(@RequestBody EmailVerificationRequest request) {
        emailService.sendSimpleMessage(request);
    }

    @PostMapping("/members/email/verify")
    public boolean verifyEmail(@RequestBody EmailVerificationRequest request) {
        return emailService.validateEmail(request);
    }

    @DeleteMapping("/members/email/expire")
    public void expireVerificationCode(EmailVerificationRequest request) {
        emailService.expireCode(request.getEmail());
    }
}

package com.heyticket.backend.controller;

import com.heyticket.backend.module.security.jwt.dto.TokenInfo;
import com.heyticket.backend.service.CacheService;
import com.heyticket.backend.service.EmailService;
import com.heyticket.backend.service.MemberService;
import com.heyticket.backend.service.dto.request.EmailSendRequest;
import com.heyticket.backend.service.dto.request.MemberCategoryUpdateRequest;
import com.heyticket.backend.service.dto.request.MemberDeleteRequest;
import com.heyticket.backend.service.dto.request.MemberKeywordUpdateRequest;
import com.heyticket.backend.service.dto.request.MemberLikeRequest;
import com.heyticket.backend.service.dto.request.MemberLoginRequest;
import com.heyticket.backend.service.dto.request.MemberSignUpRequest;
import com.heyticket.backend.service.dto.request.PasswordResetRequest;
import com.heyticket.backend.service.dto.request.TokenReissueRequest;
import com.heyticket.backend.service.dto.request.VerificationRequest;
import com.heyticket.backend.service.dto.response.CommonResponse;
import com.heyticket.backend.service.dto.response.MemberResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    private final EmailService emailService;

    private final CacheService cacheService;

    @PostMapping("/members/login")
    public ResponseEntity<CommonResponse> login(@RequestBody MemberLoginRequest request) {
        TokenInfo tokenInfo = memberService.login(request);
        return CommonResponse.ok("Login successful.", tokenInfo);
    }

    @PostMapping("/members/signup")
    public ResponseEntity<CommonResponse> signUp(@RequestBody MemberSignUpRequest request) {
        String email = memberService.signUp(request);
        return CommonResponse.ok("Sign up successful.", email);
    }

    @GetMapping("/members/{email}")
    public ResponseEntity<CommonResponse> getMember(@PathVariable String email) {
        MemberResponse memberResponse = memberService.getMemberByEmail(email);
        return CommonResponse.ok("Sign up successful.", memberResponse);
    }

    @PostMapping("/members/verification/send")
    public ResponseEntity<CommonResponse> sendSignUpVerificationEmail(@RequestBody @Valid EmailSendRequest request) {
        String email = memberService.sendVerificationEmail(request);
        return CommonResponse.ok("Verification email has been sent.", email);
    }

    @PostMapping("/members/verification/verify")
    public ResponseEntity<CommonResponse> verifyCode(@RequestBody @Valid VerificationRequest request) {
        boolean isVerified = cacheService.isValidCodeWithTime(request);
        return CommonResponse.ok("Email verification result.", isVerified);
    }

    @DeleteMapping("/members/verification/expire")
    public ResponseEntity<CommonResponse> expireVerificationCode(@RequestBody VerificationRequest request) {
        String email = emailService.expireCode(request.getEmail());
        return CommonResponse.ok("Email verification code has been expired.", email);
    }

    @PutMapping("/members/token")
    public ResponseEntity<CommonResponse> reissueJwtTokens(@RequestBody @Valid TokenReissueRequest request) {
        TokenInfo tokenInfo = memberService.reissueAccessToken(request);
        return CommonResponse.ok("Reissued token information.", tokenInfo);
    }

    @PutMapping("/members/password")
    public ResponseEntity<CommonResponse> resetPassword(@RequestBody PasswordResetRequest request) {
        String email = memberService.resetPassword(request);
        return CommonResponse.ok("Password change successful.", email);
    }

    @DeleteMapping("/members")
    public ResponseEntity<CommonResponse> deleteMember(@RequestBody MemberDeleteRequest request) {
        String deletedEmail = memberService.deleteMember(request);
        return CommonResponse.ok("Member has been deleted", deletedEmail);
    }

    @PutMapping("/members/categories")
    public ResponseEntity<CommonResponse> updateCategory(@RequestBody MemberCategoryUpdateRequest request) {
        memberService.updatePreferredCategory(request);
        return CommonResponse.ok("Member category has been updated", true);
    }

    @PutMapping("/members/keywords")
    public ResponseEntity<CommonResponse> updateKeyword(@RequestBody MemberKeywordUpdateRequest request) {
        memberService.updatePreferredKeyword(request);
        return CommonResponse.ok("Member keyword has been updated", true);
    }

    @PostMapping("/members/like")
    public ResponseEntity<CommonResponse> hitLike(@RequestBody MemberLikeRequest request) {
        memberService.hitLike(request);
        return CommonResponse.ok("Member like " + request.getPerformanceId(), true);
    }

    @DeleteMapping("/members/like")
    public ResponseEntity<CommonResponse> cancelLike(@RequestBody MemberLikeRequest request) {
        memberService.cancelLike(request);
        return CommonResponse.ok("Member cancel like " + request.getPerformanceId(), true);
    }
}

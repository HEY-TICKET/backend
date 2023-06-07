package com.heyticket.backend.controller;

import com.heyticket.backend.module.security.jwt.dto.TokenInfo;
import com.heyticket.backend.service.CacheService;
import com.heyticket.backend.service.EmailService;
import com.heyticket.backend.service.MemberLikeService;
import com.heyticket.backend.service.MemberService;
import com.heyticket.backend.service.dto.pagable.CustomPageRequest;
import com.heyticket.backend.service.dto.pagable.PageResponse;
import com.heyticket.backend.service.dto.request.EmailSendRequest;
import com.heyticket.backend.service.dto.request.MemberCategoryUpdateRequest;
import com.heyticket.backend.service.dto.request.MemberDeleteRequest;
import com.heyticket.backend.service.dto.request.MemberKeywordUpdateRequest;
import com.heyticket.backend.service.dto.request.MemberLikeListRequest;
import com.heyticket.backend.service.dto.request.MemberLikeSaveRequest;
import com.heyticket.backend.service.dto.request.MemberLoginRequest;
import com.heyticket.backend.service.dto.request.MemberSignUpRequest;
import com.heyticket.backend.service.dto.request.PasswordResetRequest;
import com.heyticket.backend.service.dto.request.TokenReissueRequest;
import com.heyticket.backend.service.dto.request.VerificationRequest;
import com.heyticket.backend.service.dto.response.CommonResponse;
import com.heyticket.backend.service.dto.response.MemberResponse;
import com.heyticket.backend.service.dto.response.PerformanceResponse;
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

    private final MemberLikeService memberLikeService;

    @PostMapping("/members/login")
    public ResponseEntity<?> login(@RequestBody MemberLoginRequest request) {
        TokenInfo tokenInfo = memberService.login(request);
        return CommonResponse.ok("Login successful.", tokenInfo);
    }

    @PostMapping("/members/signup")
    public ResponseEntity<?> signUp(@RequestBody MemberSignUpRequest request) {
        String email = memberService.signUp(request);
        return CommonResponse.ok("Sign up successful.", email);
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<?> getMember(@PathVariable String id) {
        MemberResponse memberResponse = memberService.getMemberByEmail(id);
        return CommonResponse.ok("User info.", memberResponse);
    }

    @PostMapping("/members/verification/send")
    public ResponseEntity<?> sendSignUpVerificationEmail(@RequestBody @Valid EmailSendRequest request) {
        String email = memberService.sendVerificationEmail(request);
        return CommonResponse.ok("Verification email has been sent.", email);
    }

    @PostMapping("/members/verification/verify")
    public ResponseEntity<?> verifyCode(@RequestBody @Valid VerificationRequest request) {
        boolean isVerified = cacheService.isValidCodeWithTime(request);
        return CommonResponse.ok("Email verification result.", isVerified);
    }

    @DeleteMapping("/members/verification/expire")
    public ResponseEntity<?> expireVerificationCode(@RequestBody VerificationRequest request) {
        String email = emailService.expireCode(request.getEmail());
        return CommonResponse.ok("Email verification code has been expired.", email);
    }

    @PutMapping("/members/token")
    public ResponseEntity<?> reissueJwtTokens(@RequestBody @Valid TokenReissueRequest request) {
        TokenInfo tokenInfo = memberService.reissueAccessToken(request);
        return CommonResponse.ok("Reissued token information.", tokenInfo);
    }

    @PutMapping("/members/password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        String email = memberService.resetPassword(request);
        return CommonResponse.ok("Password change successful.", email);
    }

    @DeleteMapping("/members")
    public ResponseEntity<?> deleteMember(@RequestBody MemberDeleteRequest request) {
        String deletedEmail = memberService.deleteMember(request);
        return CommonResponse.ok("Member has been deleted", deletedEmail);
    }

    @PutMapping("/members/categories")
    public ResponseEntity<?> updateCategory(@RequestBody MemberCategoryUpdateRequest request) {
        memberService.updatePreferredCategory(request);
        return CommonResponse.ok("Member category has been updated", true);
    }

    @PutMapping("/members/keywords")
    public ResponseEntity<?> updateKeyword(@RequestBody MemberKeywordUpdateRequest request) {
        memberService.updatePreferredKeyword(request);
        return CommonResponse.ok("Member keyword has been updated", true);
    }

    @GetMapping("/members/performances/like")
    public ResponseEntity<?> getMemberLikePerformances(MemberLikeListRequest request, CustomPageRequest pageRequest) {
        PageResponse<PerformanceResponse> memberLikePerformances = memberLikeService.getMemberLikedPerformances(request, pageRequest.of());
        return CommonResponse.ok("Performances member liked.", memberLikePerformances);
    }

    @PostMapping("/members/performances/like")
    public ResponseEntity<?> hitLike(@RequestBody MemberLikeSaveRequest request) {
        memberService.hitLike(request);
        return CommonResponse.ok("Member like " + request.getPerformanceId(), true);
    }

    @DeleteMapping("/members/performances/like")
    public ResponseEntity<?> cancelLike(@RequestBody MemberLikeSaveRequest request) {
        memberService.cancelLike(request);
        return CommonResponse.ok("Member cancel like " + request.getPerformanceId(), true);
    }
}

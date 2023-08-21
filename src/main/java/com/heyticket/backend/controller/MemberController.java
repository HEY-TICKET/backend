package com.heyticket.backend.controller;

import com.heyticket.backend.module.security.jwt.TokenInfo;
import com.heyticket.backend.service.KeywordService;
import com.heyticket.backend.service.MemberLikeService;
import com.heyticket.backend.service.MemberService;
import com.heyticket.backend.service.dto.pagable.CustomPageRequest;
import com.heyticket.backend.service.dto.pagable.PageResponse;
import com.heyticket.backend.service.dto.request.EmailSendRequest;
import com.heyticket.backend.service.dto.request.KeywordDeleteRequest;
import com.heyticket.backend.service.dto.request.KeywordSaveRequest;
import com.heyticket.backend.service.dto.request.MemberCategoryUpdateRequest;
import com.heyticket.backend.service.dto.request.MemberDeleteRequest;
import com.heyticket.backend.service.dto.request.MemberLikeListRequest;
import com.heyticket.backend.service.dto.request.MemberLikeSaveRequest;
import com.heyticket.backend.service.dto.request.MemberLoginRequest;
import com.heyticket.backend.service.dto.request.MemberPushUpdateRequest;
import com.heyticket.backend.service.dto.request.MemberSignUpRequest;
import com.heyticket.backend.service.dto.request.MemberValidationRequest;
import com.heyticket.backend.service.dto.request.PasswordResetRequest;
import com.heyticket.backend.service.dto.request.PasswordUpdateRequest;
import com.heyticket.backend.service.dto.request.TokenReissueRequest;
import com.heyticket.backend.service.dto.request.VerificationRequest;
import com.heyticket.backend.service.dto.response.CommonResponse;
import com.heyticket.backend.service.dto.response.MemberResponse;
import com.heyticket.backend.service.dto.response.PerformanceResponse;
import com.heyticket.backend.service.dto.swaggerresponse.BooleanCommonResponse;
import com.heyticket.backend.service.dto.swaggerresponse.MemberCommonResponse;
import com.heyticket.backend.service.dto.swaggerresponse.PagePerformanceCommonrResponse;
import com.heyticket.backend.service.dto.swaggerresponse.StringCommonResponse;
import com.heyticket.backend.service.dto.swaggerresponse.TokenInfoCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    private final MemberLikeService memberLikeService;

    private final KeywordService keywordService;

    // Unauthorized
    @Operation(summary = "로그인")
    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = TokenInfoCommonResponse.class)))})
    @PostMapping("/members/login")
    public ResponseEntity<?> login(@RequestBody @Valid MemberLoginRequest request) {
        TokenInfo tokenInfo = memberService.login(request);
        return CommonResponse.ok("Login successful.", tokenInfo);
    }

    @Operation(summary = "회원 가입")
    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = StringCommonResponse.class)))})
    @PostMapping("/members/signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid MemberSignUpRequest request) {
        TokenInfo tokenInfo = memberService.signUp(request);
        return CommonResponse.ok("Sign up successful.", tokenInfo);
    }

    @Operation(summary = "이메일 검증")
    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = BooleanCommonResponse.class)))})
    @PostMapping("/members/validation")
    public ResponseEntity<?> validateMember(@RequestBody @Valid MemberValidationRequest request) {
        boolean exist = memberService.validateMember(request);
        return CommonResponse.ok("true. if registered member.", exist);
    }

    @Operation(summary = "인증 메일 전송")
    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = StringCommonResponse.class)))})
    @PostMapping("/members/verification/send")
    public ResponseEntity<?> sendSignUpVerificationEmail(@RequestBody @Valid EmailSendRequest request) {
        String email = memberService.sendVerificationEmail(request);
        return CommonResponse.ok("Verification email has been sent.", email);
    }

    @Operation(summary = "인증 번호 확인")
    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = StringCommonResponse.class)))})
    @PostMapping("/members/verification/verify")
    public ResponseEntity<?> verifyCode(@RequestBody @Valid VerificationRequest request) {
        String verificationCode = memberService.verifyCode(request);
        return CommonResponse.ok("Email verification is successful. New verification code issued.", verificationCode);
    }

    @Operation(summary = "엑세스 토큰 만료 시 토큰 재발급")
    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = TokenInfoCommonResponse.class)))})
    @PutMapping("/members/token")
    public ResponseEntity<?> reissueJwtTokens(@RequestBody @Valid TokenReissueRequest request) {
        TokenInfo tokenInfo = memberService.reissueAccessToken(request);
        return CommonResponse.ok("Reissued token information.", tokenInfo);
    }

    @Operation(summary = "비밀번호 변경(로그인 화면)")
    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = StringCommonResponse.class)))})
    @PutMapping("/members/password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid PasswordResetRequest request) {
        String email = memberService.resetPassword(request);
        return CommonResponse.ok("Password change successful.", email);
    }

    // Authorized
    @Operation(summary = "회원 정보 조회")
    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = MemberCommonResponse.class)))})
    @GetMapping("/members/{id}")
    public ResponseEntity<?> getMember(@PathVariable String id) {
        MemberResponse memberResponse = memberService.getMemberByEmail(id);
        return CommonResponse.ok("User info.", memberResponse);
    }

    @Operation(summary = "인증 번호 만료시키기(화면 이탈 시)")
    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = StringCommonResponse.class)))})
    @DeleteMapping("/members/verification/expire")
    public ResponseEntity<?> expireVerificationCode(@RequestBody @Valid VerificationRequest request) {
        String email = memberService.expireCode(request.getEmail());
        return CommonResponse.ok("Email verification code has been expired.", email);
    }

    @Operation(summary = "비밀번호 변경(내 정보)")
    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = TokenInfoCommonResponse.class)))})
    @PutMapping("/members/password/update")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid PasswordUpdateRequest request) {
        memberService.updatePassword(request);
        return CommonResponse.ok("Password change successful.", true);
    }

    @Operation(summary = "회원 탈퇴")
    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = StringCommonResponse.class)))})
    @DeleteMapping("/members")
    public ResponseEntity<?> deleteMember(@RequestBody @Valid MemberDeleteRequest request) {
        String deletedEmail = memberService.deleteMember(request);
        return CommonResponse.ok("Member has been deleted", deletedEmail);
    }

    @Operation(summary = "회원 카테고리 수정")
    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = BooleanCommonResponse.class)))})
    @PutMapping("/members/categories")
    public ResponseEntity<?> updateCategory(@RequestBody @Valid MemberCategoryUpdateRequest request) {
        memberService.updatePreferredCategory(request);
        return CommonResponse.ok("Member category has been updated", true);
    }

    @Operation(summary = "회원 키워드 추가")
    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = BooleanCommonResponse.class)))})
    @PostMapping("/members/keywords")
    public ResponseEntity<?> saveKeyword(@RequestBody @Valid KeywordSaveRequest request) {
        keywordService.saveKeyword(request);
        return CommonResponse.ok("Member keyword has been added", true);
    }

    @Operation(summary = "회원 키워드 추가")
    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = BooleanCommonResponse.class)))})
    @DeleteMapping("/members/keywords")
    public ResponseEntity<?> deleteKeyword(@RequestBody @Valid KeywordDeleteRequest request) {
        keywordService.deleteKeyword(request);
        return CommonResponse.ok("Member keyword has been deleted", true);
    }

    @Operation(summary = "회원 찜한 공연 조회")
    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = PagePerformanceCommonrResponse.class)))})
    @GetMapping("/members/performances/like")
    public ResponseEntity<?> getMemberLikePerformances(@Valid MemberLikeListRequest request, CustomPageRequest pageRequest) {
        PageResponse<PerformanceResponse> memberLikePerformances = memberLikeService.getMemberLikedPerformances(request, pageRequest.of());
        return CommonResponse.ok("Performances member liked.", memberLikePerformances);
    }

    @Operation(summary = "공연 찜하기")
    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = BooleanCommonResponse.class)))})
    @PostMapping("/members/performances/like")
    public ResponseEntity<?> hitLike(@RequestBody MemberLikeSaveRequest request) {
        memberLikeService.hitLike(request);
        return CommonResponse.ok("Member like " + request.getPerformanceId(), true);
    }

    @Operation(summary = "공연 찜하기 취소")
    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = BooleanCommonResponse.class)))})
    @DeleteMapping("/members/performances/like")
    public ResponseEntity<?> cancelLike(@RequestBody @Valid MemberLikeSaveRequest request) {
        memberLikeService.cancelLike(request);
        return CommonResponse.ok("Member cancel like " + request.getPerformanceId(), true);
    }

    @Operation(summary = "관심 정보 알림 설정")
    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = BooleanCommonResponse.class)))})
    @PutMapping("/members/keyword-push")
    public ResponseEntity<?> updateKeywordPushEnabled(@RequestBody @Valid MemberPushUpdateRequest request) {
        memberService.updateKeywordPushEnabled(request);
        return CommonResponse.ok("Member keyword push updated.", true);
    }

    @Operation(summary = "마케팅 알림 설정")
    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = BooleanCommonResponse.class)))})
    @PutMapping("/members/marketing-push")
    public ResponseEntity<?> updateMarketingPushEnabled(@RequestBody @Valid MemberPushUpdateRequest request) {
        memberService.updateMarketingPushEnabled(request);
        return CommonResponse.ok("Member marketing push updated.", true);
    }
}

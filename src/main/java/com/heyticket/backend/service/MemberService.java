package com.heyticket.backend.service;

import com.heyticket.backend.domain.Keyword;
import com.heyticket.backend.domain.Member;
import com.heyticket.backend.domain.MemberArea;
import com.heyticket.backend.domain.MemberGenre;
import com.heyticket.backend.domain.MemberKeyword;
import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.exception.NotFoundException;
import com.heyticket.backend.exception.ValidationFailureException;
import com.heyticket.backend.module.security.jwt.JwtTokenProvider;
import com.heyticket.backend.module.security.jwt.SecurityUtil;
import com.heyticket.backend.module.security.jwt.TokenInfo;
import com.heyticket.backend.module.util.PasswordValidator;
import com.heyticket.backend.module.util.VerificationCodeGenerator;
import com.heyticket.backend.repository.member.MemberRepository;
import com.heyticket.backend.service.dto.VerificationCode;
import com.heyticket.backend.service.dto.request.EmailSendRequest;
import com.heyticket.backend.service.dto.request.MemberCategoryUpdateRequest;
import com.heyticket.backend.service.dto.request.MemberDeleteRequest;
import com.heyticket.backend.service.dto.request.MemberFcmTokenUpdateRequest;
import com.heyticket.backend.service.dto.request.MemberLoginRequest;
import com.heyticket.backend.service.dto.request.MemberPushUpdateRequest;
import com.heyticket.backend.service.dto.request.MemberSignUpRequest;
import com.heyticket.backend.service.dto.request.MemberValidationRequest;
import com.heyticket.backend.service.dto.request.PasswordResetRequest;
import com.heyticket.backend.service.dto.request.PasswordUpdateRequest;
import com.heyticket.backend.service.dto.request.TokenReissueRequest;
import com.heyticket.backend.service.dto.request.VerificationRequest;
import com.heyticket.backend.service.dto.response.MemberResponse;
import com.heyticket.backend.service.enums.Area;
import com.heyticket.backend.service.enums.AuthProvider;
import com.heyticket.backend.service.enums.Genre;
import com.heyticket.backend.service.enums.VerificationType;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    private final KeywordService keywordService;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final PasswordEncoder passwordEncoder;

    private final LocalCacheService localCacheService;

    private final IEmailService emailService;

    public MemberResponse getMemberById(Long id) {
        Member member = getMemberFromDb(id);

        List<Genre> genres = member.getMemberGenres().stream()
            .map(MemberGenre::getGenre)
            .collect(Collectors.toList());

        List<Area> areas = member.getMemberAreas().stream()
            .map(MemberArea::getArea)
            .collect(Collectors.toList());

        List<String> keywords = member.getMemberKeywords().stream()
            .map(memberKeyword -> memberKeyword.getKeyword().getContent())
            .collect(Collectors.toList());

        return MemberResponse.builder()
            .id(member.getId())
            .email(member.getEmail())
            .allowKeywordPush(member.isAllowKeywordPush())
            .allowMarketing(member.isAllowMarketing())
            .genres(genres)
            .areas(areas)
            .keywords(keywords)
            .build();
    }

    public TokenInfo signUp(MemberSignUpRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        PasswordValidator.validatePassword(password);
        checkIfExistingMember(email);
        verifyCode(email, request.getVerificationCode());

        Member member = Member.builder()
            .email(email)
            .password(passwordEncoder.encode(password))
            .roles(List.of("USER"))
            .memberAreas(new ArrayList<>())
            .memberGenres(new ArrayList<>())
            .memberKeywords(new ArrayList<>())
            .memberLikes(new ArrayList<>())
            .allowKeywordPush(request.isKeywordPush())
            .authProvider(AuthProvider.EMAIL)
            .build();

        List<MemberGenre> memberGenres = request.getGenres().stream()
            .map(MemberGenre::of)
            .collect(Collectors.toList());

        List<MemberArea> memberAreas = request.getAreas().stream()
            .map(MemberArea::of)
            .collect(Collectors.toList());

        List<MemberKeyword> memberKeywords = request.getKeywords().stream()
            .map(content -> {
                Keyword keyword = keywordService.getOrSave(content);

                return MemberKeyword.builder()
                    .member(member)
                    .keyword(keyword)
                    .build();
            })
            .collect(Collectors.toList());

        member.addMemberGenres(memberGenres);
        member.addMemberAreas(memberAreas);
        member.addMemberKeywords(memberKeywords);

        Member savedMember = memberRepository.save(member);

        TokenInfo tokenInfo = jwtTokenProvider.generateToken(savedMember.getId(), member.getStrAuthorities());
        localCacheService.putRefreshToken(savedMember.getId(), tokenInfo.getRefreshToken());

        return tokenInfo;
    }

    public void updateFcmToken(Long id, MemberFcmTokenUpdateRequest request) {
        Member member = getMemberFromDb(id);
        member.updateFcmToken(request.getToken());
    }

    public TokenInfo login(MemberLoginRequest request) {
        Member member = memberRepository.findByEmailAndAuthProvider(request.getEmail(), AuthProvider.EMAIL)
            .orElseThrow(() -> new NotFoundException("No such user.", InternalCode.NOT_FOUND));

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenInfo tokenInfo = jwtTokenProvider.generateToken(member.getId(), member.getStrAuthorities());
        localCacheService.putRefreshToken(member.getId(), tokenInfo.getRefreshToken());
        return tokenInfo;
    }

    public void updatePassword(PasswordUpdateRequest request) {
        String email = SecurityUtil.getCurrentMemberEmail();
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("No such member.", InternalCode.NOT_FOUND));
        matchPassword(request.getCurrentPassword(), member.getPassword());
        if (request.getNewPassword().equals(request.getCurrentPassword())) {
            throw new ValidationFailureException("The new password is identical to the existing password.", InternalCode.BAD_REQUEST);
        }
        PasswordValidator.validatePassword(request.getNewPassword());
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        member.updatePassword(encodedPassword);
    }

    public boolean validateMember(MemberValidationRequest request) {
        return memberRepository.existsByEmailAndAuthProvider(request.getEmail(), AuthProvider.EMAIL);
    }

    private void checkIfExistingMember(String email) {
        boolean existingEmail = memberRepository.existsByEmailAndAuthProvider(email, AuthProvider.EMAIL);
        if (existingEmail) {
            throw new ValidationFailureException("Duplicated email exists.", InternalCode.EXISTING_EMAIL);
        }
    }

    public TokenInfo reissueAccessToken(TokenReissueRequest request) {
        Long memberId = request.getId();
        String refreshToken = request.getRefreshToken();
        jwtTokenProvider.validateToken(refreshToken);
        String savedRefreshToken = localCacheService.getRefreshTokenIfPresent(memberId);
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new NotFoundException("No such refresh token information.", InternalCode.NOT_FOUND);
        }
        Member member = getMemberFromDb(memberId);
        String authorities = member.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(memberId, authorities);
        localCacheService.putRefreshToken(memberId, tokenInfo.getRefreshToken());
        return tokenInfo;
    }

    public Long resetPassword(PasswordResetRequest request) {
        String code = request.getVerificationCode();
        Member member = getMemberFromDb(request.getId());

        PasswordValidator.validatePassword(request.getPassword());
        boolean matched = passwordEncoder.matches(request.getPassword(), member.getPassword());
        if (matched) {
            throw new ValidationFailureException("The new password is identical to the existing password.", InternalCode.BAD_REQUEST);
        }

        verifyCode(member.getEmail(), code);
        member.updatePassword(passwordEncoder.encode(request.getPassword()));
        localCacheService.invalidateRefreshToken(member.getId());
        return request.getId();
    }

    public String sendVerificationEmail(EmailSendRequest request) {
        String email = request.getEmail();
        if (request.getVerificationType() == VerificationType.SIGN_UP) {
            checkIfExistingMember(email);
        }
        emailService.sendSimpleMessage(request);
        return email;
    }

    public Long deleteMember(MemberDeleteRequest request) {
        Member member = getMemberFromDb(request.getId());
        matchPassword(request.getPassword(), member.getPassword());
        memberRepository.delete(member);
        localCacheService.invalidateRefreshToken(member.getId());
        //todo keyword만 남은 경우 keyword 삭제
        return member.getId();
    }

    private void matchPassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new ValidationFailureException("Wrong password.", InternalCode.PW_MISMATCH);
        }
    }

    public void updatePreferredCategory(MemberCategoryUpdateRequest request) {
        Member member = getMemberFromDb(request.getId());

        if (request.getGenres() != null) {
            List<MemberGenre> memberGenres = member.getMemberGenres();

            List<Genre> genres = Genre.getByNames(request.getGenres());
            List<MemberGenre> newMemberGenres = genres.stream()
                .map(MemberGenre::of)
                .collect(Collectors.toList());

            memberGenres.removeIf(memberGenre -> !newMemberGenres.contains(memberGenre));
            newMemberGenres.stream()
                .filter(newMemberGenre -> !memberGenres.contains(newMemberGenre))
                .forEach(memberGenres::add);
        }

        if (request.getAreas() != null) {
            List<MemberArea> memberAreas = member.getMemberAreas();

            List<Area> areas = Area.getByNames(request.getAreas());
            List<MemberArea> newMemberAreas = areas.stream()
                .map(MemberArea::of)
                .collect(Collectors.toList());

            memberAreas.removeIf(memberArea -> !newMemberAreas.contains(memberArea));
            newMemberAreas.stream()
                .filter(newMemberArea -> !memberAreas.contains(newMemberArea))
                .forEach(memberAreas::add);
        }
    }

    public void updateKeywordPushEnabled(MemberPushUpdateRequest request) {
        Member member = getMemberFromDb(request.getId());
        member.setAllowKeywordPush(request.isPushEnabled());
    }

    public void updateMarketingPushEnabled(MemberPushUpdateRequest request) {
        Member member = getMemberFromDb(request.getId());
        member.setAllowMarketing(request.isPushEnabled());
    }

    public String expireCode(String email) {
        localCacheService.invalidateVerificationCode(email);
        return email;
    }

    public String verifyCode(VerificationRequest request) {
        boolean validCodeWithTime = localCacheService.isValidVerificationCodeWithTime(request);
        if (!validCodeWithTime) {
            throw new ValidationFailureException("Verification code is outdated or not matched.", InternalCode.VERIFICATION_FAILURE);
        }

        String code = VerificationCodeGenerator.createCode();
        VerificationCode verificationCode = VerificationCode.of(code, System.currentTimeMillis() + 600000);
        localCacheService.putVerificationCode(request.getEmail(), verificationCode);

        return code;
    }

    private Member getMemberFromDb(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("No such member.", InternalCode.NOT_FOUND));
    }

    private void verifyCode(String email, String code) {
        VerificationRequest request = VerificationRequest.builder()
            .email(email)
            .code(code)
            .build();

        boolean validCode = localCacheService.isValidVerificationCode(request);
        if (!validCode) {
            throw new ValidationFailureException("Validation code failure.", InternalCode.VERIFICATION_FAILURE);
        }
        localCacheService.invalidateVerificationCode(email);
    }
}

package com.heyticket.backend.service;

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
import com.heyticket.backend.service.dto.request.MemberKeywordUpdateRequest;
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
import com.heyticket.backend.service.enums.Genre;
import com.heyticket.backend.service.enums.VerificationType;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final LocalCacheService localCacheService;

    private final EmailService emailService;

    public MemberResponse getMemberByEmail(String email) {
        Member member = getMemberFromDb(email);

        List<Genre> genres = member.getMemberGenres().stream()
            .map(MemberGenre::getGenre)
            .collect(Collectors.toList());

        List<Area> areas = member.getMemberAreas().stream()
            .map(MemberArea::getArea)
            .collect(Collectors.toList());

        List<String> keywords = member.getMemberKeywords().stream()
            .map(MemberKeyword::getKeyword)
            .collect(Collectors.toList());

        return MemberResponse.builder()
            .email(email)
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
            .build();

        List<MemberGenre> memberGenres = request.getGenres().stream()
            .map(MemberGenre::of)
            .collect(Collectors.toList());

        List<MemberArea> memberAreas = request.getAreas().stream()
            .map(MemberArea::of)
            .collect(Collectors.toList());

        List<MemberKeyword> memberKeywords = request.getKeywords().stream()
            .map(MemberKeyword::of)
            .collect(Collectors.toList());

        member.addMemberGenres(memberGenres);
        member.addMemberAreas(memberAreas);
        member.addMemberKeywords(memberKeywords);

        memberRepository.save(member);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        localCacheService.putRefreshToken(request.getEmail(), tokenInfo.getRefreshToken());

        return tokenInfo;
    }

    public TokenInfo login(MemberLoginRequest request) {
        boolean exists = memberRepository.existsByEmail(request.getEmail());
        if (!exists) {
            throw new NotFoundException("No such user.", InternalCode.NOT_FOUND);
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        localCacheService.putRefreshToken(request.getEmail(), tokenInfo.getRefreshToken());
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
        return memberRepository.existsByEmail(request.getEmail());
    }

    private void checkIfExistingMember(String email) {
        boolean existingEmail = memberRepository.existsByEmail(email);
        if (existingEmail) {
            throw new ValidationFailureException("Duplicated email exists.", InternalCode.EXISTING_EMAIL);
        }
    }

    public TokenInfo reissueAccessToken(TokenReissueRequest request) {
        String email = request.getEmail();
        String refreshToken = request.getRefreshToken();
        jwtTokenProvider.validateToken(refreshToken);
        String savedRefreshToken = localCacheService.getRefreshTokenIfPresent(email);
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new NotFoundException("No such refresh token information.", InternalCode.NOT_FOUND);
        }
        Member member = getMemberFromDb(email);
        String authorities = member.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
        TokenInfo tokenInfo = jwtTokenProvider.regenerateToken(email, authorities);
        localCacheService.putRefreshToken(email, tokenInfo.getRefreshToken());
        return tokenInfo;
    }

    public String resetPassword(PasswordResetRequest request) {
        String email = request.getEmail();
        String code = request.getVerificationCode();
        Member member = getMemberFromDb(email);
        verifyCode(email, code);

        PasswordValidator.validatePassword(request.getPassword());
        boolean matched = passwordEncoder.matches(request.getPassword(), member.getPassword());
        if (matched) {
            throw new ValidationFailureException("The new password is identical to the existing password.", InternalCode.BAD_REQUEST);
        }
        member.updatePassword(passwordEncoder.encode(request.getPassword()));
        localCacheService.invalidateRefreshToken(email);
        return email;
    }

    public String sendVerificationEmail(EmailSendRequest request) {
        String email = request.getEmail();
        if (request.getVerificationType() == VerificationType.SIGN_UP) {
            checkIfExistingMember(email);
        }
        emailService.sendSimpleMessage(request);
        return email;
    }

    public String deleteMember(MemberDeleteRequest request) {
        String email = request.getEmail();
        Member member = getMemberFromDb(email);
        matchPassword(request.getPassword(), member.getPassword());
        memberRepository.delete(member);
        localCacheService.invalidateRefreshToken(email);
        return email;
    }

    private void matchPassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new ValidationFailureException("Wrong password.", InternalCode.PW_MISMATCH);
        }
    }

    public void updatePreferredCategory(MemberCategoryUpdateRequest request) {
        Member member = getMemberFromDb(request.getEmail());

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

    public void updatePreferredKeyword(MemberKeywordUpdateRequest request) {
        Member member = getMemberFromDb(request.getEmail());

        if (request.getKeywords() != null) {
            List<MemberKeyword> memberKeywords = member.getMemberKeywords();

            List<String> keywords = request.getKeywords();
            List<MemberKeyword> newMemberKeywords = keywords.stream()
                .map(MemberKeyword::of)
                .collect(Collectors.toList());

            memberKeywords.removeIf(memberKeyword -> !newMemberKeywords.contains(memberKeyword));
            newMemberKeywords.stream()
                .filter(newMemberKeyword -> !memberKeywords.contains(newMemberKeyword))
                .forEach(memberKeywords::add);
        }
    }

    public void updateKeywordPushEnabled(MemberPushUpdateRequest request) {
        Member member = getMemberFromDb(request.getEmail());
        member.setAllowKeywordPush(request.isPushEnabled());
    }

    public void updateMarketingPushEnabled(MemberPushUpdateRequest request) {
        Member member = getMemberFromDb(request.getEmail());
        member.setAllowMarketing(request.isPushEnabled());
    }

    public String expireCode(String email) {
        localCacheService.invalidateCode(email);
        return email;
    }

    public String verifyCode(VerificationRequest request) {
        boolean validCodeWithTime = localCacheService.isValidCodeWithTime(request);
        if (!validCodeWithTime) {
            throw new ValidationFailureException("Verification code is outdated or not matched.", InternalCode.VERIFICATION_FAILURE);
        }

        String code = VerificationCodeGenerator.createCode();
        VerificationCode verificationCode = VerificationCode.of(code, System.currentTimeMillis() + 600000);
        localCacheService.putVerificationCode(request.getEmail(), verificationCode);

        return code;
    }

    private Member getMemberFromDb(String email) {
        return memberRepository.findById(email)
            .orElseThrow(() -> new NotFoundException("No such member.", InternalCode.NOT_FOUND));
    }

    private void verifyCode(String email, String code) {
        VerificationRequest request = VerificationRequest.builder()
            .email(email)
            .code(code)
            .build();

        boolean validCode = localCacheService.isValidCode(request);
        if (!validCode) {
            throw new ValidationFailureException("Validation code failure.", InternalCode.VERIFICATION_FAILURE);
        }
        localCacheService.invalidateCode(email);
    }
}

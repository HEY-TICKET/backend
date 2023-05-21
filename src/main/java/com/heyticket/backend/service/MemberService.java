package com.heyticket.backend.service;

import com.heyticket.backend.domain.Member;
import com.heyticket.backend.domain.MemberArea;
import com.heyticket.backend.domain.MemberGenre;
import com.heyticket.backend.domain.MemberKeyword;
import com.heyticket.backend.module.kopis.enums.Area;
import com.heyticket.backend.module.kopis.enums.Genre;
import com.heyticket.backend.module.security.jwt.JwtTokenProvider;
import com.heyticket.backend.module.security.jwt.dto.TokenInfo;
import com.heyticket.backend.module.util.PasswordValidator;
import com.heyticket.backend.repository.MemberAreaRepository;
import com.heyticket.backend.repository.MemberGenreRepository;
import com.heyticket.backend.repository.MemberKeywordRepository;
import com.heyticket.backend.repository.MemberRepository;
import com.heyticket.backend.service.dto.request.EmailSendRequest;
import com.heyticket.backend.service.dto.request.MemberDeleteRequest;
import com.heyticket.backend.service.dto.request.MemberLoginRequest;
import com.heyticket.backend.service.dto.request.MemberSignUpRequest;
import com.heyticket.backend.service.dto.request.PasswordResetRequest;
import com.heyticket.backend.service.dto.request.TokenReissueRequest;
import com.heyticket.backend.service.enums.VerificationType;
import jakarta.transaction.Transactional;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.NoSuchElementException;
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

    private final MemberGenreRepository memberGenreRepository;

    private final MemberAreaRepository memberAreaRepository;

    private final MemberKeywordRepository memberKeywordRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final CacheService cacheService;

    private final EmailService emailService;

    public String signUp(MemberSignUpRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        PasswordValidator.validatePassword(password);
        checkIfExistingMember(email);
        validateCode(email, request.getVerificationCode());

        Member member = Member.builder()
            .email(email)
            .password(passwordEncoder.encode(password))
            .roles(List.of("USER"))
            .build();

        Member savedMember = memberRepository.save(member);

        List<Genre> genres = Genre.getByNames(request.getGenres());
        List<MemberGenre> memberGenres = getMemberGenres(savedMember, genres);
        memberGenreRepository.saveAll(memberGenres);

        List<Area> areas = Area.getByNames(request.getAreas());
        List<MemberArea> memberAreas = getMemberAreas(savedMember, areas);
        memberAreaRepository.saveAll(memberAreas);

        List<String> keywords = request.getKeywords();
        List<MemberKeyword> memberKeywords = getMemberKeywords(savedMember, keywords);
        memberKeywordRepository.saveAll(memberKeywords);
        return email;
    }

    public TokenInfo login(MemberLoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        cacheService.putRefreshToken(request.getEmail(), tokenInfo.getRefreshToken());
        return tokenInfo;
    }

    public void checkIfExistingMember(String email) {
        boolean existingEmail = memberRepository.existsByEmail(email);
        if (existingEmail) {
            throw new InvalidParameterException("Duplicated email exists.");
        }
    }

    public TokenInfo reissueAccessToken(TokenReissueRequest request) {
        String email = request.getEmail();
        String refreshToken = request.getRefreshToken();
        jwtTokenProvider.validateToken(refreshToken);
        String savedRefreshToken = cacheService.getRefreshTokenIfPresent(email);
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new NoSuchElementException("No such refresh token information.");
        }
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("No such user"));
        String authorities = member.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
        TokenInfo tokenInfo = jwtTokenProvider.regenerateToken(email, authorities);
        cacheService.putRefreshToken(email, tokenInfo.getRefreshToken());
        return tokenInfo;
    }

    public String resetPassword(PasswordResetRequest request) {
        String email = request.getEmail();
        String code = request.getVerificationCode();
        String savedCode = cacheService.getCodeIfPresent(email);
        if (savedCode == null) {
            throw new NoSuchElementException("인증 내역이 없습니다");
        }
        if (!savedCode.equals(code)) {
            throw new IllegalStateException("인증 내역이 다릅니다.");
        }
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("No such member"));
        member.updatePassword(passwordEncoder.encode(request.getPassword()));
        cacheService.invalidateRefreshToken(email);
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
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("No such member email"));
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new InvalidParameterException("Wrong password.");
        }
        memberRepository.delete(member);
        cacheService.invalidateRefreshToken(email);
        return email;
    }

    private List<MemberGenre> getMemberGenres(Member member, List<Genre> genres) {
        return genres.stream()
            .map(genre ->
                MemberGenre.builder()
                    .genre(genre)
                    .member(member)
                    .build()
            )
            .collect(Collectors.toList());
    }

    private List<MemberArea> getMemberAreas(Member member, List<Area> areas) {
        return areas.stream()
            .map(area ->
                MemberArea.builder()
                    .area(area)
                    .member(member)
                    .build()
            )
            .collect(Collectors.toList());
    }

    private List<MemberKeyword> getMemberKeywords(Member member, List<String> keywords) {
        return keywords.stream()
            .map(keyword ->
                MemberKeyword.builder()
                    .keyword(keyword)
                    .member(member)
                    .build()
            )
            .collect(Collectors.toList());
    }

    private void validateCode(String email, String code) {
        String savedCode = cacheService.getCodeIfPresent(email);
        if (savedCode == null) {
            throw new IllegalStateException("No such email verification info.");
        }
        if (!savedCode.equals(code)) {
            throw new IllegalStateException("Validation code is wrong.");
        }
        cacheService.invalidateCode(email);
    }
}

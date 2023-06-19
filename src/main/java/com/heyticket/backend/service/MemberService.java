package com.heyticket.backend.service;

import com.heyticket.backend.domain.Member;
import com.heyticket.backend.domain.MemberArea;
import com.heyticket.backend.domain.MemberGenre;
import com.heyticket.backend.domain.MemberKeyword;
import com.heyticket.backend.domain.MemberLike;
import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.exception.LoginFailureException;
import com.heyticket.backend.module.kopis.enums.Area;
import com.heyticket.backend.module.kopis.enums.Genre;
import com.heyticket.backend.module.security.jwt.JwtTokenProvider;
import com.heyticket.backend.module.security.jwt.SecurityUtil;
import com.heyticket.backend.module.security.jwt.dto.TokenInfo;
import com.heyticket.backend.module.util.PasswordValidator;
import com.heyticket.backend.repository.MemberAreaRepository;
import com.heyticket.backend.repository.MemberGenreRepository;
import com.heyticket.backend.repository.MemberKeywordRepository;
import com.heyticket.backend.repository.MemberLikeRepository;
import com.heyticket.backend.repository.MemberRepository;
import com.heyticket.backend.repository.PerformanceRepository;
import com.heyticket.backend.service.dto.request.EmailSendRequest;
import com.heyticket.backend.service.dto.request.MemberCategoryUpdateRequest;
import com.heyticket.backend.service.dto.request.MemberDeleteRequest;
import com.heyticket.backend.service.dto.request.MemberKeywordUpdateRequest;
import com.heyticket.backend.service.dto.request.MemberLikeSaveRequest;
import com.heyticket.backend.service.dto.request.MemberLoginRequest;
import com.heyticket.backend.service.dto.request.MemberSignUpRequest;
import com.heyticket.backend.service.dto.request.MemberValidationRequest;
import com.heyticket.backend.service.dto.request.PasswordResetRequest;
import com.heyticket.backend.service.dto.request.TokenReissueRequest;
import com.heyticket.backend.service.dto.request.VerificationRequest;
import com.heyticket.backend.service.dto.response.MemberResponse;
import com.heyticket.backend.service.enums.VerificationType;
import jakarta.transaction.Transactional;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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

    private final MemberLikeRepository memberLikeRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final PerformanceRepository performanceRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final CacheService cacheService;

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
        validateCode(email, request.getVerificationCode());

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

        List<Genre> genres = Genre.getByNames(request.getGenres());
        List<MemberGenre> memberGenres = genres.stream()
            .map(MemberGenre::of)
            .collect(Collectors.toList());

        List<Area> areas = Area.getByNames(request.getAreas());
        List<MemberArea> memberAreas = areas.stream()
            .map(MemberArea::of)
            .collect(Collectors.toList());

        List<String> keywords = request.getKeywords();
        List<MemberKeyword> memberKeywords = keywords.stream()
            .map(MemberKeyword::of)
            .collect(Collectors.toList());

        member.addMemberGenres(memberGenres);
        member.addMemberAreas(memberAreas);
        member.addMemberKeywords(memberKeywords);

        memberRepository.save(member);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        cacheService.putRefreshToken(request.getEmail(), tokenInfo.getRefreshToken());

        return tokenInfo;
    }

    public TokenInfo login(MemberLoginRequest request) {
        boolean exists = memberRepository.existsByEmail(request.getEmail());
        if (!exists) {
            throw new LoginFailureException("No such user.", InternalCode.USER_NOT_FOUND);
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        cacheService.putRefreshToken(request.getEmail(), tokenInfo.getRefreshToken());
        return tokenInfo;
    }

    public boolean validateMember(MemberValidationRequest request) {
        return memberRepository.existsByEmail(request.getEmail());
    }

    private void checkIfExistingMember(String email) {
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
        Member member = getMemberFromDb(email);
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
        String savedCode = cacheService.getVerificationCodeIfPresent(email).getCode();
        if (savedCode == null) {
            throw new NoSuchElementException("인증 내역이 없습니다");
        }
        if (!savedCode.equals(code)) {
            throw new IllegalStateException("인증 내역이 다릅니다.");
        }
        Member member = getMemberFromDb(email);
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
        Member member = getMemberFromDb(email);
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new InvalidParameterException("Wrong password.");
        }
        memberRepository.delete(member);
        cacheService.invalidateRefreshToken(email);
        return email;
    }

    public void likePerformance(String performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
            .orElseThrow(() -> new NoSuchElementException("No such performance"));

        Member member = getCurrentMember();

        MemberLike memberLike = MemberLike.builder()
            .member(member)
            .performance(performance)
            .build();

        memberLikeRepository.save(memberLike);
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

    public void hitLike(MemberLikeSaveRequest request) {
        Member member = getMemberFromDb(request.getEmail());
        String performanceId = request.getPerformanceId();
        Performance performance = performanceRepository.findById(performanceId)
            .orElseThrow(() -> new NoSuchElementException("No such performance."));
        Optional<MemberLike> optionalMemberLike = memberLikeRepository.findMemberLikeByMemberAndPerformance(member, performance);
        if (optionalMemberLike.isPresent()) {
            return;
        }
        MemberLike memberLike = MemberLike.of(member, performance);
        memberLikeRepository.save(memberLike);
    }

    public void cancelLike(MemberLikeSaveRequest request) {
        Member member = getMemberFromDb(request.getEmail());
        String performanceId = request.getPerformanceId();
        Performance performance = performanceRepository.findById(performanceId)
            .orElseThrow(() -> new NoSuchElementException("No such performance."));
        Optional<MemberLike> optionalMemberLike = memberLikeRepository.findMemberLikeByMemberAndPerformance(member, performance);
        if (optionalMemberLike.isEmpty()) {
            return;
        }
        memberLikeRepository.deleteByMemberAndPerformance(member, performance);
    }

    private Member getMemberFromDb(String email) {
        return memberRepository.findById(email)
            .orElseThrow(() -> new NoSuchElementException("No such member."));
    }

    private Member getCurrentMember() {
        String email = SecurityUtil.getCurrentMemberEmail();
        return getMemberFromDb(email);
    }

    private void validateCode(String email, String code) {
        VerificationRequest request = VerificationRequest.builder()
            .email(email)
            .code(code)
            .build();

        boolean validCode = cacheService.isValidCode(request);
        if (!validCode) {
            throw new IllegalStateException();
        }
        cacheService.invalidateCode(email);
    }
}

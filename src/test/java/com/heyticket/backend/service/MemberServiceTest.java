package com.heyticket.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.heyticket.backend.domain.Member;
import com.heyticket.backend.domain.MemberArea;
import com.heyticket.backend.domain.MemberGenre;
import com.heyticket.backend.domain.MemberKeyword;
import com.heyticket.backend.exception.NotFoundException;
import com.heyticket.backend.exception.ValidationFailureException;
import com.heyticket.backend.module.security.jwt.TokenInfo;
import com.heyticket.backend.repository.member.MemberGenreRepository;
import com.heyticket.backend.repository.member.MemberKeywordRepository;
import com.heyticket.backend.repository.member.MemberLikeRepository;
import com.heyticket.backend.repository.member.MemberRepository;
import com.heyticket.backend.service.dto.VerificationCode;
import com.heyticket.backend.service.dto.request.MemberDeleteRequest;
import com.heyticket.backend.service.dto.request.MemberLoginRequest;
import com.heyticket.backend.service.dto.request.MemberPushUpdateRequest;
import com.heyticket.backend.service.dto.request.MemberSignUpRequest;
import com.heyticket.backend.service.dto.request.MemberValidationRequest;
import com.heyticket.backend.service.dto.request.PasswordResetRequest;
import com.heyticket.backend.service.dto.request.PasswordUpdateRequest;
import com.heyticket.backend.service.dto.response.MemberResponse;
import com.heyticket.backend.service.enums.Area;
import com.heyticket.backend.service.enums.Genre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberLikeRepository memberLikeRepository;

    @Autowired
    private MemberGenreRepository memberGenreRepository;

    @Autowired
    private MemberKeywordRepository memberKeywordRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private LocalCacheService localCacheService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_PASSWORD = "Password123";

    @AfterEach
    void deleteAll() {
        memberGenreRepository.deleteAll();
        memberLikeRepository.deleteAll();
        memberKeywordRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("Member 조회 - 데이터 확인")
    void getMemberByEmail() {
        //given
        Member member = createMember("email");
        MemberGenre memberGenre = MemberGenre.of(Genre.MUSICAL);
        MemberArea memberArea = MemberArea.of(Area.BUSAN);
        MemberKeyword memberKeyword = MemberKeyword.of("keyword");
        member.addMemberGenres(List.of(memberGenre));
        member.addMemberAreas(List.of(memberArea));
        member.addMemberKeywords(List.of(memberKeyword));
        memberRepository.save(member);

        //when
        MemberResponse memberResponse = memberService.getMemberByEmail(member.getEmail());

        //then
        assertThat(memberResponse.getEmail()).isEqualTo(member.getEmail());
        assertThat(memberResponse.getKeywords()).hasSize(1);
        assertThat(memberResponse.getAreas()).hasSize(1);
        assertThat(memberResponse.getGenres()).hasSize(1);
    }

    @Test
    @DisplayName("Member 조회 - 존재하지 않는 회원인 경우 throw NotFoundException")
    void getMemberByEmail_noSuchMember() {
        //given
        Member member = createMember("email");
        MemberGenre memberGenre = MemberGenre.of(Genre.MUSICAL);
        MemberArea memberArea = MemberArea.of(Area.BUSAN);
        MemberKeyword memberKeyword = MemberKeyword.of("keyword");
        member.addMemberGenres(List.of(memberGenre));
        member.addMemberAreas(List.of(memberArea));
        member.addMemberKeywords(List.of(memberKeyword));
        memberRepository.save(member);

        //when
        Throwable throwable = catchThrowable(() -> memberService.getMemberByEmail("wrongEmail"));

        //then
        assertThat(throwable).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Member 가입 - 데이터 확인")
    @Transactional
    void signUp() {
        //given
        MemberSignUpRequest request = MemberSignUpRequest.builder()
            .email("email")
            .password(TEST_PASSWORD)
            .areas(List.of(Area.GYEONGGI, Area.SEOUL))
            .genres(List.of(Genre.MUSICAL, Genre.THEATER))
            .keywords(List.of("맘마미아"))
            .verificationCode("verificationCode")
            .build();

        localCacheService.putVerificationCode("email", VerificationCode.of(request.getVerificationCode()));

        //when
        memberService.signUp(request);

        entityManager.flush();
        entityManager.clear();

        //then
        Member foundMember = memberRepository.findById(request.getEmail())
            .orElseThrow(() -> new NoSuchElementException("No such user."));

        assertThat(foundMember.getEmail()).isEqualTo(request.getEmail());
        assertThat(foundMember.getMemberGenres()).extracting("genre").containsOnly(Genre.MUSICAL, Genre.THEATER);
        assertThat(foundMember.getMemberAreas()).extracting("area").containsOnly(Area.GYEONGGI, Area.SEOUL);
        assertThat(foundMember.getMemberKeywords().get(0).getKeyword()).isEqualTo(request.getKeywords().get(0));
    }

    @Test
    @DisplayName("Member 가입 - 비밀번호 양식이 맞지 않으면 throw ValidationFailureException")
    void signUp_passwordValidationFailure() {
        //given
        MemberSignUpRequest request = MemberSignUpRequest.builder()
            .email("email")
            .password("password123")
            .areas(List.of(Area.GYEONGGI, Area.SEOUL))
            .genres(List.of(Genre.MUSICAL, Genre.THEATER))
            .keywords(List.of("맘마미아"))
            .verificationCode("verificationCode")
            .build();

        localCacheService.putVerificationCode("email", VerificationCode.of("verificationCode"));

        //when
        Throwable throwable = catchThrowable(() -> memberService.signUp(request));

        //then
        assertThat(throwable).isInstanceOf(ValidationFailureException.class);
    }

    @Test
    @DisplayName("Member 가입 - 이미 가입된 회원이면 throw validationException")
    void signUp_duplicateEmail() {
        //given
        Member member = createMember("email");
        memberRepository.save(member);

        MemberSignUpRequest request = MemberSignUpRequest.builder()
            .email("email")
            .password(TEST_PASSWORD)
            .areas(List.of(Area.GYEONGGI, Area.SEOUL))
            .genres(List.of(Genre.MUSICAL, Genre.THEATER))
            .keywords(List.of("맘마미아"))
            .verificationCode("verificationCode")
            .build();

        localCacheService.putVerificationCode("email", VerificationCode.of("verificationCode"));

        //when
        Throwable throwable = catchThrowable(() -> memberService.signUp(request));

        //then
        assertThat(throwable).isInstanceOf(ValidationFailureException.class);
    }

    @Test
    @DisplayName("Member 가입 - 잘못된 인증 코드를 입력 받은 경우 throw ValidationFailureException")
    void signUp_verificationCodeFailure() {
        //given
        MemberSignUpRequest request = MemberSignUpRequest.builder()
            .email("email")
            .password(TEST_PASSWORD)
            .areas(List.of(Area.GYEONGGI, Area.SEOUL))
            .genres(List.of(Genre.MUSICAL, Genre.THEATER))
            .keywords(List.of("맘마미아"))
            .verificationCode("wrongVerificationCode")
            .build();

        localCacheService.putVerificationCode("email", VerificationCode.of("verificationCode"));

        //when
        Throwable throwable = catchThrowable(() -> memberService.signUp(request));

        //then
        assertThat(throwable).isInstanceOf(ValidationFailureException.class);
    }

    @Test
    @DisplayName("Member 로그인 - 데이터 확인")
    void login_success() {
        //given
        Member member = createMember("email");
        memberRepository.save(member);

        //when
        MemberLoginRequest request = MemberLoginRequest.builder()
            .email(member.getEmail())
            .password(TEST_PASSWORD)
            .build();

        TokenInfo tokenInfo = memberService.login(request);

        //then
        assertThat(tokenInfo.getAccessToken()).isNotNull();
        assertThat(tokenInfo.getRefreshToken()).isNotNull();
    }

    @Test
    @DisplayName("Member 로그인 - 존재하지 않는 email을 입력한 경우 throw NotFoundException")
    void login_wrongEmail() {
        //given
        Member member = createMember("email");
        memberRepository.save(member);

        //when
        MemberLoginRequest request = MemberLoginRequest.builder()
            .email("wrongEmail")
            .password(TEST_PASSWORD)
            .build();

        Throwable throwable = catchThrowable(() -> memberService.login(request));

        //then
        assertThat(throwable).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Member 로그인 - 잘못된 비밀번호를 입력한 경우 throw BadCredentialException")
    void login_wrongPassword() {
        //given
        Member member = createMember("email");
        memberRepository.save(member);

        //when
        MemberLoginRequest request = MemberLoginRequest.builder()
            .email(member.getEmail())
            .password("wrongPassword")
            .build();

        Throwable throwable = catchThrowable(() -> memberService.login(request));

        //then
        assertThat(throwable).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("Member 비밀번호 변경 - 데이터 확인")
    void updatePassword() {
        //given
        Member member = createMember("email");
        memberRepository.save(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //when
        PasswordUpdateRequest request = PasswordUpdateRequest.builder()
            .currentPassword(TEST_PASSWORD)
            .newPassword("NewPassword123")
            .build();

        memberService.updatePassword(request);

        //then
        Member foundMember = memberRepository.findByEmail(member.getEmail())
            .orElseThrow(() -> new NoSuchElementException("No such member."));
        assertThat(passwordEncoder.matches(request.getNewPassword(), foundMember.getPassword())).isTrue();
    }

    @Test
    @DisplayName("Member 비밀번호 변경 - 로그인이 되지 않은 상태에서 요청이 온 경우 throw NotFoundException")
    void updatePassword_memberNotFound() {
        //given

        //when
        PasswordUpdateRequest request = PasswordUpdateRequest.builder()
            .currentPassword(TEST_PASSWORD)
            .newPassword("NewPassword123")
            .build();

        Throwable throwable = catchThrowable(() -> memberService.updatePassword(request));

        //then
        assertThat(throwable).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Member 비밀번호 변경 - 기존 비밀번호 일치하지 않는 경우 throw ValidationFailureException")
    void updatePassword_wrongPassword() {
        //given
        Member member = createMember("email");
        memberRepository.save(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //when
        PasswordUpdateRequest request = PasswordUpdateRequest.builder()
            .currentPassword("WrongPassword123")
            .newPassword("NewPassword123")
            .build();

        Throwable throwable = catchThrowable(() -> memberService.updatePassword(request));

        //then
        assertThat(throwable).isInstanceOf(ValidationFailureException.class);
    }

    @Test
    @DisplayName("Member 비밀번호 변경 - 기존 비밀번호와 새로운 번호가 일치하면 throw ValidationFailureException")
    void updatePassword_samePassword() {
        //given
        Member member = createMember("email");
        memberRepository.save(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //when
        PasswordUpdateRequest request = PasswordUpdateRequest.builder()
            .currentPassword(TEST_PASSWORD)
            .newPassword(TEST_PASSWORD)
            .build();

        Throwable throwable = catchThrowable(() -> memberService.updatePassword(request));

        //then
        assertThat(throwable).isInstanceOf(ValidationFailureException.class);
    }

    @Test
    @DisplayName("Member 탈퇴 - 데이터 확인")
    void deleteMember() {
        //given
        Member member = createMember("email");
        memberRepository.save(member);

        //when
        MemberDeleteRequest request = MemberDeleteRequest.builder()
            .email(member.getEmail())
            .password(TEST_PASSWORD)
            .build();

        memberService.deleteMember(request);

        //then
        Optional<Member> optionalMember = memberRepository.findByEmail(member.getEmail());
        assertThat(optionalMember).isNotPresent();
    }

    @Test
    @DisplayName("Member 탈퇴 - 존재하지 않는 회원인 경우 throw NotFoundException")
    void deleteMember_noSuchMember() {
        //given

        //when
        MemberDeleteRequest request = MemberDeleteRequest.builder()
            .email("wrongEmail")
            .password(TEST_PASSWORD)
            .build();

        Throwable throwable = catchThrowable(() -> memberService.deleteMember(request));

        //then
        assertThat(throwable).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Member 탈퇴 - password 일치하지 않는 경우 throw ValidationFailureException")
    void deleteMember_wrongPassword() {
        //given
        Member member = createMember("email");
        memberRepository.save(member);

        //when
        MemberDeleteRequest request = MemberDeleteRequest.builder()
            .email(member.getEmail())
            .password("wrongPassword")
            .build();

        Throwable throwable = catchThrowable(() -> memberService.deleteMember(request));

        //then
        assertThat(throwable).isInstanceOf(ValidationFailureException.class);
    }

    @Test
    @DisplayName("Member 비밀번호 변경(로그인 화면) - 데이터 확인")
    void resetPassword(){
        //given
        Member member = createMember("email");
        memberRepository.save(member);

        String verificationCode = "verificationCode";
        localCacheService.putVerificationCode(member.getEmail(), VerificationCode.of(verificationCode));

        //when
        PasswordResetRequest request = PasswordResetRequest.builder()
            .email(member.getEmail())
            .password("newPassword123")
            .verificationCode(verificationCode)
            .build();

        memberService.resetPassword(request);

        //then
        Member foundMember = memberRepository.findByEmail(member.getEmail())
            .orElseThrow(() -> new NoSuchElementException("No such member"));
        String password = foundMember.getPassword();
        boolean matched = passwordEncoder.matches(request.getPassword(), password);
        assertThat(matched).isTrue();
    }

    @Test
    @DisplayName("Member 비밀번호 변경(로그인 화면) - 해당 member가 존재하지 않는 경우 throw NotFoundException")
    void resetPassword_noSuchMember(){
        //given

        //when
        PasswordResetRequest request = PasswordResetRequest.builder()
            .email("wrongEmail")
            .password("newPassword123")
            .verificationCode("verificationCode")
            .build();

        Throwable throwable = catchThrowable(() -> memberService.resetPassword(request));

        //then
        assertThat(throwable).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Member 비밀번호 변경(로그인 화면) - 데이터 확인")
    void resetPassword_wrongVerificationCode(){
        //given
        Member member = createMember("email");
        memberRepository.save(member);

        String verificationCode = "verificationCode";
        localCacheService.putVerificationCode(member.getEmail(), VerificationCode.of(verificationCode));

        //when
        PasswordResetRequest request = PasswordResetRequest.builder()
            .email(member.getEmail())
            .password("newPassword123")
            .verificationCode("wrongVerificationCode")
            .build();

        Throwable throwable = catchThrowable(() -> memberService.resetPassword(request));

        //then
        assertThat(throwable).isInstanceOf(ValidationFailureException.class);
    }

    @Test
    @DisplayName("Member 존재 유무 확인 - 존재할 경우 return true")
    void validateMember_valid() {
        //given
        Member member = createMember("email");
        memberRepository.save(member);

        //when
        MemberValidationRequest request = MemberValidationRequest.builder()
            .email(member.getEmail())
            .build();

        boolean valid = memberService.validateMember(request);

        //then
        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("Member 존재 유무 확인 - 존재하지 않는 경우 return true")
    void validateMember_invalid() {
        //given

        //when
        MemberValidationRequest request = MemberValidationRequest.builder()
            .email("email")
            .build();

        boolean valid = memberService.validateMember(request);

        //then
        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("Member keywordPushEnable")
    void keywordPushEnable(){
        //given
        String testEmail = "testEmail";
        Member member = createMember(testEmail);
        member.setAllowKeywordPush(false);

        memberRepository.save(member);

        //when
        MemberPushUpdateRequest request = MemberPushUpdateRequest.builder()
            .email(testEmail)
            .isPushEnabled(true)
            .build();

        memberService.updateKeywordPushEnabled(request);

        //then
        Member foundMember = memberRepository.findByEmail(testEmail)
            .orElseThrow(() -> new NotFoundException("No such member"));
        assertThat(foundMember.isAllowKeywordPush()).isTrue();
    }

    @Test
    @DisplayName("Member marketingPushEnable")
    void marketingPushEnable(){
        //given
        String testEmail = "testEmail";
        Member member = createMember(testEmail);
        member.setAllowMarketing(false);

        memberRepository.save(member);

        //when
        MemberPushUpdateRequest request = MemberPushUpdateRequest.builder()
            .email(testEmail)
            .isPushEnabled(true)
            .build();

        memberService.updateMarketingPushEnabled(request);

        //then
        Member foundMember = memberRepository.findByEmail(testEmail)
            .orElseThrow(() -> new NotFoundException("No such member"));
        assertThat(foundMember.isAllowMarketing()).isTrue();
    }

    private Member createMember(String email) {
        return Member.builder()
            .email(email)
            .password(passwordEncoder.encode(TEST_PASSWORD))
            .memberAreas(new ArrayList<>())
            .memberGenres(new ArrayList<>())
            .memberLikes(new ArrayList<>())
            .memberKeywords(new ArrayList<>())
            .build();
    }
}
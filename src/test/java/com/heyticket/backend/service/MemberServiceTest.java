package com.heyticket.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.heyticket.backend.domain.Keyword;
import com.heyticket.backend.domain.Member;
import com.heyticket.backend.domain.MemberArea;
import com.heyticket.backend.domain.MemberGenre;
import com.heyticket.backend.domain.MemberKeyword;
import com.heyticket.backend.exception.NotFoundException;
import com.heyticket.backend.exception.ValidationFailureException;
import com.heyticket.backend.module.security.jwt.TokenInfo;
import com.heyticket.backend.module.util.VerificationCodeGenerator;
import com.heyticket.backend.repository.keyword.KeywordRepository;
import com.heyticket.backend.repository.member.MemberGenreRepository;
import com.heyticket.backend.repository.member.MemberKeywordRepository;
import com.heyticket.backend.repository.member.MemberLikeRepository;
import com.heyticket.backend.repository.member.MemberRepository;
import com.heyticket.backend.service.dto.request.EmailSendRequest;
import com.heyticket.backend.service.dto.request.MemberDeleteRequest;
import com.heyticket.backend.service.dto.request.MemberFcmTokenUpdateRequest;
import com.heyticket.backend.service.dto.request.MemberLoginRequest;
import com.heyticket.backend.service.dto.request.MemberPushUpdateRequest;
import com.heyticket.backend.service.dto.request.MemberSignUpRequest;
import com.heyticket.backend.service.dto.request.MemberValidationRequest;
import com.heyticket.backend.service.dto.request.PasswordResetRequest;
import com.heyticket.backend.service.dto.request.PasswordUpdateRequest;
import com.heyticket.backend.service.dto.request.VerificationRequest;
import com.heyticket.backend.service.dto.response.MemberResponse;
import com.heyticket.backend.service.enums.Area;
import com.heyticket.backend.service.enums.AuthProvider;
import com.heyticket.backend.service.enums.Genre;
import com.heyticket.backend.service.enums.VerificationType;
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
import org.springframework.boot.test.mock.mockito.MockBean;
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
    private KeywordRepository keywordRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private LocalCacheService localCacheService;

    @MockBean
    private IEmailService emailService;

    @PersistenceContext
    private EntityManager entityManager;

    private static final String TEST_PASSWORD = "Password123";

    @AfterEach
    void deleteAll() {
        memberGenreRepository.deleteAll();
        memberLikeRepository.deleteAll();
        memberKeywordRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("Member 조회 - 성공한 경우 데이터 확인")
    void getMemberByEmail() {
        //given
        Member member = createMember("email");
        MemberGenre memberGenre = MemberGenre.of(Genre.MUSICAL);
        MemberArea memberArea = MemberArea.of(Area.BUSAN);
        member.addMemberGenres(List.of(memberGenre));
        member.addMemberAreas(List.of(memberArea));

        Keyword keyword1 = Keyword.of("keyword1");
        Keyword keyword2 = Keyword.of("keyword2");

        Member savedMember = memberRepository.save(member);
        keywordRepository.saveAll(List.of(keyword1, keyword2));

        MemberKeyword memberKeyword1 = MemberKeyword.builder()
            .member(member)
            .keyword(keyword1)
            .build();

        MemberKeyword memberKeyword2 = MemberKeyword.builder()
            .member(member)
            .keyword(keyword2)
            .build();
        memberKeywordRepository.saveAll(List.of(memberKeyword1, memberKeyword2));

        entityManager.flush();
        entityManager.clear();

        //when
        MemberResponse memberResponse = memberService.getMemberById(savedMember.getId());

        //then
        assertThat(memberResponse.getEmail()).isEqualTo(member.getEmail());
        assertThat(memberResponse.getAreas()).hasSize(1);
        assertThat(memberResponse.getGenres()).hasSize(1);
        assertThat(memberResponse.getKeywords()).hasSize(2);
        assertThat(memberResponse.getKeywords()).containsExactlyElementsOf(List.of(keyword1.getContent(), keyword2.getContent()));
    }

    @Test
    @DisplayName("Member 조회 - 존재하지 않는 회원인 경우 throw NotFoundException")
    void getMemberByEmail_noSuchMember() {
        //given
        Member member = createMember("email");
        MemberGenre memberGenre = MemberGenre.of(Genre.MUSICAL);
        MemberArea memberArea = MemberArea.of(Area.BUSAN);
        member.addMemberGenres(List.of(memberGenre));
        member.addMemberAreas(List.of(memberArea));
        Member savedMember = memberRepository.save(member);

        //when
        Throwable throwable = catchThrowable(() -> memberService.getMemberById(savedMember.getId() + 1000L));

        //then
        assertThat(throwable).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Member 가입 - 성공한 경우 데이터 확인")
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

        given(localCacheService.isValidVerificationCode(any(VerificationRequest.class))).willReturn(true);

        //when
        memberService.signUp(request);

        entityManager.flush();
        entityManager.clear();

        //then
        List<Member> foundMembers = memberRepository.findAll();
        assertThat(foundMembers).hasSize(1);
        Member foundMember = foundMembers.get(0);

        assertThat(foundMember.getEmail()).isEqualTo(request.getEmail());
        assertThat(foundMember.getMemberGenres()).extracting("genre").containsOnly(Genre.MUSICAL, Genre.THEATER);
        assertThat(foundMember.getMemberAreas()).extracting("area").containsOnly(Area.GYEONGGI, Area.SEOUL);
        assertThat(foundMember.getMemberKeywords().get(0).getKeyword().getContent()).isEqualTo(request.getKeywords().get(0));
        List<Keyword> foundKeywords = keywordRepository.findAll();
        assertThat(foundKeywords).hasSize(1);
        Keyword foundKeyword = foundKeywords.get(0);
        assertThat(foundKeyword.getContent()).isEqualTo(request.getKeywords().get(0));
        List<MemberKeyword> foundMemberKeywords = memberKeywordRepository.findAll();
        assertThat(foundMemberKeywords).hasSize(1);
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

        given(localCacheService.isValidVerificationCode(any(VerificationRequest.class))).willReturn(true);

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

        given(localCacheService.isValidVerificationCode(any(VerificationRequest.class))).willReturn(true);

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

        given(localCacheService.isValidVerificationCode(any(VerificationRequest.class))).willReturn(false);

        //when
        Throwable throwable = catchThrowable(() -> memberService.signUp(request));

        //then
        assertThat(throwable).isInstanceOf(ValidationFailureException.class);
    }

    @Test
    @DisplayName("Member FCM token 업데이트 - 기존에 등록된 token이 없는 경우 등록한다")
    void updateFcmToken_registerToken(){
        //given
        Member member = createMember("email");
        Member savedMember = memberRepository.save(member);

        //when
        String token = "token";

        MemberFcmTokenUpdateRequest request = MemberFcmTokenUpdateRequest.builder()
            .token(token)
            .build();

        memberService.updateFcmToken(savedMember.getId(), request);

        //then
        Member foundMember = memberRepository.findById(savedMember.getId())
            .orElseThrow(() -> new NotFoundException("No such member"));
        assertThat(foundMember.getFcmToken()).isEqualTo(token);
     }

    @Test
    @DisplayName("Member FCM token 업데이트 - 기존에 등록된 token이 있는 경우 업데이트한다")
    void updateFcmToken_updateToken(){
        //given
        Member member = createMember("email");
        member.updateFcmToken("oldToken");
        Member savedMember = memberRepository.save(member);

        //when
        String token = "token";

        MemberFcmTokenUpdateRequest request = MemberFcmTokenUpdateRequest.builder()
            .token(token)
            .build();

        memberService.updateFcmToken(savedMember.getId(), request);

        //then
        Member foundMember = memberRepository.findById(savedMember.getId())
            .orElseThrow(() -> new NotFoundException("No such member"));
        assertThat(foundMember.getFcmToken()).isEqualTo(token);
    }

    @Test
    @DisplayName("Member 로그인 - 성공한 경우 데이터 확인")
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
    @DisplayName("Member 비밀번호 변경 - 성공한 경우 데이터 확인")
    void updatePassword() {
        //given
        Member member = createMember("email");
        Member savedMember = memberRepository.save(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //when
        PasswordUpdateRequest request = PasswordUpdateRequest.builder()
            .currentPassword(TEST_PASSWORD)
            .newPassword("NewPassword123")
            .build();

        memberService.updatePassword(request);

        //then
        Member foundMember = memberRepository.findById(savedMember.getId())
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
    @DisplayName("Member 탈퇴 - 성공한 경우 데이터 확인")
    void deleteMember() {
        //given
        Member member = createMember("email");
        Member savedMember = memberRepository.save(member);

        //when
        MemberDeleteRequest request = MemberDeleteRequest.builder()
            .id(member.getId())
            .password(TEST_PASSWORD)
            .build();

        memberService.deleteMember(request);

        //then
        Optional<Member> optionalMember = memberRepository.findById(savedMember.getId());
        assertThat(optionalMember).isNotPresent();
    }

    @Test
    @DisplayName("Member 탈퇴 - 존재하지 않는 회원인 경우 throw NotFoundException")
    void deleteMember_noSuchMember() {
        //given

        //when
        MemberDeleteRequest request = MemberDeleteRequest.builder()
            .id(111L)
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
        Member savedMember = memberRepository.save(member);

        //when
        MemberDeleteRequest request = MemberDeleteRequest.builder()
            .id(savedMember.getId())
            .password("wrongPassword")
            .build();

        Throwable throwable = catchThrowable(() -> memberService.deleteMember(request));

        //then
        assertThat(throwable).isInstanceOf(ValidationFailureException.class);
    }

    @Test
    @DisplayName("Member 비밀번호 변경(로그인 화면) - 성공한 경우 데이터 확인")
    void resetPassword() {
        //given
        Member member = createMember("email");
        Member savedMember = memberRepository.save(member);

        given(localCacheService.isValidVerificationCode(any(VerificationRequest.class))).willReturn(true);

        //when
        PasswordResetRequest request = PasswordResetRequest.builder()
            .id(savedMember.getId())
            .password("newPassword123")
            .verificationCode("verificationCode")
            .build();

        memberService.resetPassword(request);

        //then
        Member foundMember = memberRepository.findById(savedMember.getId())
            .orElseThrow(() -> new NoSuchElementException("No such member"));
        String password = foundMember.getPassword();
        boolean matched = passwordEncoder.matches(request.getPassword(), password);
        assertThat(matched).isTrue();
    }

    @Test
    @DisplayName("Member 비밀번호 변경(로그인 화면) - 해당 member가 존재하지 않는 경우 throw NotFoundException")
    void resetPassword_noSuchMember() {
        //given

        //when
        PasswordResetRequest request = PasswordResetRequest.builder()
            .id(111L)
            .password("newPassword123")
            .verificationCode("verificationCode")
            .build();

        Throwable throwable = catchThrowable(() -> memberService.resetPassword(request));

        //then
        assertThat(throwable).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Member 비밀번호 변경(로그인 화면) - 잘못된 인증코드인 경우")
    void resetPassword_wrongVerificationCode() {
        //given
        Member member = createMember("email");
        Member savedMember = memberRepository.save(member);

        given(localCacheService.isValidVerificationCode(any(VerificationRequest.class))).willReturn(false);

        //when
        PasswordResetRequest request = PasswordResetRequest.builder()
            .id(savedMember.getId())
            .password("newPassword123")
            .verificationCode("wrongVerificationCode")
            .build();

        Throwable throwable = catchThrowable(() -> memberService.resetPassword(request));

        //then
        assertThat(throwable).isInstanceOf(ValidationFailureException.class);
    }

    @Test
    @DisplayName("Member 비밀번호 변경(로그인 화면) - 기존과 동일한 비밀번호인 경우 throw ValidationFailureException")
    void resetPassword_samePassword() {
        //given
        Member member = createMember("email");
        Member savedMember = memberRepository.save(member);

        given(localCacheService.isValidVerificationCode(any(VerificationRequest.class))).willReturn(true);

        //when
        PasswordResetRequest request = PasswordResetRequest.builder()
            .id(savedMember.getId())
            .password(TEST_PASSWORD)
            .verificationCode("verificationCode")
            .build();

        Throwable throwable = catchThrowable(() -> memberService.resetPassword(request));

        //then
        assertThat(throwable).isInstanceOf(ValidationFailureException.class);
        assertThat(throwable.getMessage()).isEqualTo("The new password is identical to the existing password.");
    }

    @Test
    @DisplayName("Member 비밀번호 변경(로그인 화면) - 변경 비밀번호가 기존 비밀번호와 동일한 경우 인증번호를 만료시키지 않는다")
    void resetPassword_ifSamePasswordMaintainVerificationCode() {
        //given
        Member member = createMember("email");
        Member savedMember = memberRepository.save(member);

        //when
        PasswordResetRequest request = PasswordResetRequest.builder()
            .id(savedMember.getId())
            .password(TEST_PASSWORD)
            .verificationCode("verificationCode")
            .build();

        Throwable throwable = catchThrowable(() -> memberService.resetPassword(request));

        //then
        assertThat(throwable).isInstanceOf(ValidationFailureException.class);
        then(localCacheService).should(never()).invalidateRefreshToken(member.getEmail());
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
    @DisplayName("Member 키워드 푸시 허용 여부 변경")
    void keywordPushEnable() {
        //given
        String testEmail = "testEmail";
        Member member = createMember(testEmail);
        member.setAllowKeywordPush(false);

        Member savedMember = memberRepository.save(member);

        //when
        MemberPushUpdateRequest request = MemberPushUpdateRequest.builder()
            .id(savedMember.getId())
            .isPushEnabled(true)
            .build();

        memberService.updateKeywordPushEnabled(request);

        //then
        Member foundMember = memberRepository.findById(savedMember.getId())
            .orElseThrow(() -> new NotFoundException("No such member"));
        assertThat(foundMember.isAllowKeywordPush()).isTrue();
    }

    @Test
    @DisplayName("Member 마케팅 푸시 허용 여부 변경")
    void marketingPushEnable() {
        //given
        String testEmail = "testEmail";
        Member member = createMember(testEmail);
        member.setAllowMarketing(false);

        Member savedMember = memberRepository.save(member);

        //when
        MemberPushUpdateRequest request = MemberPushUpdateRequest.builder()
            .id(savedMember.getId())
            .isPushEnabled(true)
            .build();

        memberService.updateMarketingPushEnabled(request);

        //then
        Member foundMember = memberRepository.findById(savedMember.getId())
            .orElseThrow(() -> new NotFoundException("No such member"));
        assertThat(foundMember.isAllowMarketing()).isTrue();
    }

    @Test
    @DisplayName("Verification code 검증 - verification code 검증 성공하면 새로운 verification code를 발급한다")
    void verifyCode(){
        //given
        VerificationRequest request = VerificationRequest.builder()
            .email("email")
            .code("code")
            .build();

        given(localCacheService.isValidVerificationCodeWithTime(any(VerificationRequest.class))).willReturn(true);

        //when
        String newCode = memberService.verifyCode(request);

        //then
        assertThat(newCode).isNotEqualTo(request.getCode());
        assertThat(newCode.length()).isEqualTo(VerificationCodeGenerator.createCode().length());
     }

    @Test
    @DisplayName("Verification code 검증 - verification code 검증 실패하면 throw ValidationFailureException")
    void verifyCode_noVerificationCode(){
        //given
        VerificationRequest request = VerificationRequest.builder()
            .email("email")
            .code("code")
            .build();

        given(localCacheService.isValidVerificationCodeWithTime(any(VerificationRequest.class))).willReturn(false);

        //when
        Throwable throwable = catchThrowable(() -> memberService.verifyCode(request));

        //then
        assertThat(throwable).isInstanceOf(ValidationFailureException.class);
    }

    @Test
    @DisplayName("Verification code 전송 - 성공 경우 데이터 확인")
    void sendVerificationEmail(){
        //given
        EmailSendRequest request = EmailSendRequest.builder()
            .email("email")
            .verificationType(VerificationType.SIGN_UP)
            .build();

        //when
        String email = memberService.sendVerificationEmail(request);

        //then
        assertThat(email).isEqualTo(request.getEmail());
        then(emailService).should().sendSimpleMessage(request);
     }

    @Test
    @DisplayName("Verification code 전송 - 회원 가입 타입이고 이미 존재하는 member인 경우 throw ValidationFailureException")
    void sendVerificationEmail_existingMember(){
        //given
        EmailSendRequest request = EmailSendRequest.builder()
            .email("email")
            .verificationType(VerificationType.SIGN_UP)
            .build();

        Member existingMember = createMember(request.getEmail());
        memberRepository.save(existingMember);

        //when
        Throwable throwable = catchThrowable(() -> memberService.sendVerificationEmail(request));

        //then
        assertThat(throwable).isInstanceOf(ValidationFailureException.class);
    }

    private Member createMember(String email) {
        return Member.builder()
            .email(email)
            .authProvider(AuthProvider.EMAIL)
            .password(passwordEncoder.encode(TEST_PASSWORD))
            .memberAreas(new ArrayList<>())
            .memberGenres(new ArrayList<>())
            .memberLikes(new ArrayList<>())
            .memberKeywords(new ArrayList<>())
            .build();
    }
}
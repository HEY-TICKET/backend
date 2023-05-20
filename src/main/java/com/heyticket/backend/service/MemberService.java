package com.heyticket.backend.service;

import com.heyticket.backend.domain.Member;
import com.heyticket.backend.module.security.jwt.JwtTokenProvider;
import com.heyticket.backend.module.security.jwt.dto.TokenInfo;
import com.heyticket.backend.module.util.PasswordValidator;
import com.heyticket.backend.repository.MemberRepository;
import com.heyticket.backend.service.dto.MemberLoginRequest;
import com.heyticket.backend.service.dto.MemberSignUpRequest;
import jakarta.transaction.Transactional;
import java.security.InvalidParameterException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
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

    private final CacheService cacheService;

    public void signUp(MemberSignUpRequest request) {
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

        memberRepository.save(member);
    }

    private void validateCode(String email, String code) {
        String savedCode = cacheService.getIfPresent(email);
        if (savedCode == null) {
            throw new IllegalStateException("invalid verification email");
        }
        if (!savedCode.equals(code)) {
            throw new IllegalStateException("fail to verification");
        }
        cacheService.invalidate(email);
    }

    public TokenInfo login(MemberLoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        return jwtTokenProvider.generateToken(authentication);
    }

    public void checkIfExistingMember(String email) {
        boolean existingEmail = memberRepository.existsByEmail(email);
        if (existingEmail) {
            throw new InvalidParameterException("Duplicated email exists.");
        }
    }
}

package com.heyticket.backend.service;

import com.heyticket.backend.module.security.jwt.JwtTokenProvider;
import com.heyticket.backend.module.security.jwt.dto.TokenInfo;
import com.heyticket.backend.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenInfo login(String memberId, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberId, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        return tokenInfo;
    }
}

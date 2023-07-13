package com.heyticket.backend.module.security.jwt;

import com.heyticket.backend.domain.Member;
import com.heyticket.backend.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByEmail(username)
            .map(this::createUserDetails)
            .orElseThrow(() -> new UsernameNotFoundException("No such user."));
    }

    private UserDetails createUserDetails(Member member) {
        return User.builder()
            .username(member.getUsername())
            .password(member.getPassword())
            .roles(member.getRoles().toArray(new String[0]))
            .build();
    }
}

package com.heyticket.backend.service;

import com.heyticket.backend.domain.Member;
import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.exception.NotFoundException;
import com.heyticket.backend.module.security.oauth.OAuth2UserInfoFactory;
import com.heyticket.backend.repository.member.MemberRepository;
import com.heyticket.backend.service.dto.MemberInfo;
import com.heyticket.backend.service.dto.OAuth2UserInfo;
import com.heyticket.backend.service.enums.AuthProvider;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    private final LocalCacheService cacheService;

    private static final String OAUTH_PW_PREFIX = "pw";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        AuthProvider authProvider = AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(authProvider, oAuth2User.getAttributes());

        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            log.error("Email is not found in oAuth2UserInfo");
            throw new NotFoundException("Email data for oAuth2UserInfo is empty", InternalCode.SERVER_ERROR);
        }

        Optional<Member> optionalMember = memberRepository.findByEmailAndAuthProvider(oAuth2UserInfo.getEmail(), authProvider);
        if (optionalMember.isEmpty()) {
            MemberInfo memberInfo = MemberInfo.builder()
                .email(oAuth2UserInfo.getEmail())
                .password(OAUTH_PW_PREFIX + oAuth2UserInfo.getOAuth2Id())
                .authProvider(authProvider)
                .build();
            cacheService.putMemberInfo(oAuth2UserInfo.getOAuth2Id(), memberInfo);
        }

        return oAuth2User;
    }
}

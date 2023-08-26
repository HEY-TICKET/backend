package com.heyticket.backend.module.security.oauth;

import com.heyticket.backend.service.dto.OAuth2UserInfo;
import com.heyticket.backend.service.enums.AuthProvider;
import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(AuthProvider authProvider, Map<String, Object> attributes) {
        if (authProvider == AuthProvider.KAKAO) {
            return new KakaoOAuth2User(attributes);
        }
        throw new IllegalArgumentException("Invalid Provider Type.");
    }
}
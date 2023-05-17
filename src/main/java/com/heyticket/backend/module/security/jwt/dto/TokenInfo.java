package com.heyticket.backend.module.security.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class TokenInfo {

    private String grantType;

    private String accessToken;

    private String refreshToken;

}

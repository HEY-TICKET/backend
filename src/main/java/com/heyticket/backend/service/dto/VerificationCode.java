package com.heyticket.backend.service.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VerificationCode {

    private final String code;

    private final long expirationTime;

    public static VerificationCode of(String code, long expirationTime) {
        return new VerificationCode(code, expirationTime);
    }

    public static VerificationCode of(String code) {
        return new VerificationCode(code, 4114760954000L);
    }
}

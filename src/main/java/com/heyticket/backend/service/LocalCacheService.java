package com.heyticket.backend.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.exception.NotFoundException;
import com.heyticket.backend.service.dto.MemberInfo;
import com.heyticket.backend.service.dto.VerificationCode;
import com.heyticket.backend.service.dto.request.VerificationRequest;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LocalCacheService {

    private final Cache<String, VerificationCode> emailVerificationCache;

    private final Cache<String, String> refreshTokenCache;

    private final Cache<String, MemberInfo> memberInfoCache;

    public LocalCacheService(@Value("${jwt.expiration.refresh}") long refreshTokenExpirationMillis) {
        emailVerificationCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

        refreshTokenCache = CacheBuilder.newBuilder()
            .expireAfterWrite(refreshTokenExpirationMillis, TimeUnit.MILLISECONDS)
            .build();

        memberInfoCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    }

    public VerificationCode getVerificationCodeIfPresent(String email) {
        return emailVerificationCache.getIfPresent(email);
    }

    public String getRefreshTokenIfPresent(String token) {
        return refreshTokenCache.getIfPresent(token);
    }

    public MemberInfo getMemberInfoIfPresent(String code) {
        return memberInfoCache.getIfPresent(code);
    }

    public void putVerificationCode(String email, VerificationCode verificationCode) {
        emailVerificationCache.put(email, verificationCode);
    }

    public void putRefreshToken(String email, String token) {
        refreshTokenCache.put(email, token);
    }

    public void putMemberInfo(String code, MemberInfo memberInfo) {
        memberInfoCache.put(code, memberInfo);
    }

    public void invalidateVerificationCode(String email) {
        emailVerificationCache.invalidate(email);
    }

    public void invalidateOAuth2IdCode(String oAuth2Id) {
        memberInfoCache.invalidate(oAuth2Id);
    }

    public void invalidateRefreshToken(String email) {
        refreshTokenCache.invalidate(email);
    }

    public boolean isValidVerificationCodeWithTime(VerificationRequest request) {
        VerificationCode verificationCode = getVerificationCode(request.getEmail());
        boolean isExpired = verificationCode.getExpirationTime() < System.currentTimeMillis();

        return !isExpired && verificationCode.getCode().equals(request.getCode());
    }

    public boolean isValidVerificationCode(VerificationRequest request) {
        VerificationCode verificationCode = getVerificationCode(request.getEmail());

        return verificationCode.getCode().equals(request.getCode());
    }

    private VerificationCode getVerificationCode(String email) {
        VerificationCode verificationCode = getVerificationCodeIfPresent(email);
        if (verificationCode == null) {
            throw new NotFoundException("Verification code for this email does not exist.", InternalCode.EXPIRED_CODE);
        }
        return verificationCode;
    }
}

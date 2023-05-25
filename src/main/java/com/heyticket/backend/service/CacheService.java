package com.heyticket.backend.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.heyticket.backend.service.dto.VerificationCode;
import com.heyticket.backend.service.dto.request.VerificationRequest;
import jakarta.annotation.PostConstruct;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheService {

    private Cache<String, VerificationCode> emailVerificationCache;

    private Cache<String, String> refreshTokenCache;

    @Value("${jwt.expiration.refresh}")
    private long RefreshTokenExpirationMillis;

    @PostConstruct
    public void setUpCache() {
        emailVerificationCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

        refreshTokenCache = CacheBuilder.newBuilder()
            .expireAfterWrite(RefreshTokenExpirationMillis, TimeUnit.MILLISECONDS)
            .build();
    }

    public void putVerificationCode(String email, VerificationCode verificationCode) {
        emailVerificationCache.put(email, verificationCode);
    }

    public VerificationCode getVerificationCodeIfPresent(String email) {
        return emailVerificationCache.getIfPresent(email);
    }

    public void invalidateCode(String email) {
        emailVerificationCache.invalidate(email);
    }

    public void putRefreshToken(String email, String token) {
        refreshTokenCache.put(email, token);
    }

    public String getRefreshTokenIfPresent(String token) {
        return refreshTokenCache.getIfPresent(token);
    }

    public void invalidateRefreshToken(String email) {
        refreshTokenCache.invalidate(email);
    }

    public boolean isValidCodeWithTime(VerificationRequest request) {
        VerificationCode verificationCode = getVerificationCode(request.getEmail());
        boolean isExpired = verificationCode.getExpirationTime() < System.currentTimeMillis();

        return !isExpired && verificationCode.getCode().equals(request.getCode());
    }

    public boolean isValidCode(VerificationRequest request) {
        VerificationCode verificationCode = getVerificationCode(request.getEmail());

        return verificationCode.getCode().equals(request.getCode());
    }

    private VerificationCode getVerificationCode(String email) {
        VerificationCode verificationCode = getVerificationCodeIfPresent(email);
        if (verificationCode == null) {
            throw new NoSuchElementException("해당 메일의 인증 내역이 없습니다.");
        }
        return verificationCode;
    }

}
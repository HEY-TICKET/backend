package com.heyticket.backend.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheService {

    private Cache<String, String> emailVerificationCache;

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

    public void putCode(String email, String code) {
        emailVerificationCache.put(email, code);
    }

    public String getCodeIfPresent(String email) {
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

}

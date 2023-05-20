package com.heyticket.backend.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheService {

    private Cache<String, String> verificationCodeCache;

    @PostConstruct
    public void setUpCache() {
        verificationCodeCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    }

    public void put(String key, String value) {
        verificationCodeCache.put(key, value);
    }

    public String getIfPresent(String key) {
        return verificationCodeCache.getIfPresent(key);
    }

    public void invalidate(String key) {
        verificationCodeCache.invalidate(key);
    }

}

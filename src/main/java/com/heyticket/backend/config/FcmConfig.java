package com.heyticket.backend.config;

import com.heyticket.backend.service.DummyFcmService;
import com.heyticket.backend.service.FcmService;
import com.heyticket.backend.service.IFcmService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class FcmConfig {

    @Bean
    @Profile({"prod", "prodnoauth", "localnoauth"})
    public IFcmService fcmService() {
        return new FcmService();
    }

    @Bean
    @ConditionalOnMissingBean(IFcmService.class)
    public IFcmService dummyFcmService() {
        return new DummyFcmService();
    }
}

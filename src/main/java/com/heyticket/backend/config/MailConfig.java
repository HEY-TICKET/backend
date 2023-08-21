package com.heyticket.backend.config;

import com.heyticket.backend.config.properties.SmtpProperties;
import com.heyticket.backend.config.properties.SmtpProperties.Smtp;
import com.heyticket.backend.service.DummyEmailService;
import com.heyticket.backend.service.EmailService;
import com.heyticket.backend.service.IEmailService;
import com.heyticket.backend.service.LocalCacheService;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@RequiredArgsConstructor
public class MailConfig {

    private final SmtpProperties smtpProperties;

    @Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setUsername(smtpProperties.getAdminId());
        javaMailSender.setPassword(smtpProperties.getAdminPw());
        javaMailSender.setPort(smtpProperties.getSmtp().getPort());
        javaMailSender.setJavaMailProperties(getMailProperties());
        javaMailSender.setDefaultEncoding("UTF-8");
        return javaMailSender;
    }

    @Bean
    @Profile({"prod", "prodnoauth", "localnoauth"})
    public IEmailService emailService(JavaMailSender mailSender, LocalCacheService localCacheService) {
        return new EmailService(mailSender, localCacheService);
    }

    @Bean
    @ConditionalOnMissingBean(type = "IEmailService")
    public IEmailService dummyEmailService() {
        return new DummyEmailService();
    }

    private Properties getMailProperties() {
        Smtp smtp = smtpProperties.getSmtp();
        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.socketFactory.port", smtp.getSocketFactory().getPort());
        properties.put("mail.smtp.auth", smtp.isAuth());
        properties.put("mail.smtp.starttls.enable", smtp.getStarttls().isEnable());
        properties.put("mail.smtp.starttls.required", smtp.getStarttls().isRequired());
        properties.put("mail.smtp.socketFactory.fallback", smtp.getSocketFactory().isFallback());
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        return properties;
    }
}


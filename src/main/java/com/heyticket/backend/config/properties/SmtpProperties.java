package com.heyticket.backend.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "mail")
@Component
@Getter
@Setter
public class SmtpProperties {

    private Smtp smtp;

    private String adminId;

    private String adminPw;

    @Getter
    @Setter
    public static class Smtp {

        private boolean auth;

        private StartTls starttls;

        private SocketFactory socketFactory;

        private int port;

        @Getter
        @Setter
        public static class StartTls {

            private boolean required;

            private boolean enable;
        }

        @Getter
        @Setter
        public static class SocketFactory {

            private String clazz;

            private boolean fallback;

            private int port;

        }
    }
}

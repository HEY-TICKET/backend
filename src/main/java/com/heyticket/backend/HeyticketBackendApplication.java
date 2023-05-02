package com.heyticket.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableFeignClients
@EnableJpaAuditing
@SpringBootApplication
public class HeyticketBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeyticketBackendApplication.class, args);
    }

}

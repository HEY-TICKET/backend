package com.heyticket.backend.service.dto.request;

import lombok.Getter;

@Getter
public class PasswordResetRequest {

    private String email;

    private String password;

    private String verificationCode;
}

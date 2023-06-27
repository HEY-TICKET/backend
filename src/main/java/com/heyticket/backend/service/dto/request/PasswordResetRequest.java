package com.heyticket.backend.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordResetRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String verificationCode;
}

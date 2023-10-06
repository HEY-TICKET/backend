package com.heyticket.backend.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequest {

    @NotBlank
    private Long id;

    @NotBlank
    private String password;

    @NotBlank
    private String verificationCode;
}

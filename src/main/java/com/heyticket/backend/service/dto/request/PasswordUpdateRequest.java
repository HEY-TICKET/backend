package com.heyticket.backend.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordUpdateRequest {

    @NotBlank
    private String currentPassword;

    @NotBlank
    private String newPassword;
}

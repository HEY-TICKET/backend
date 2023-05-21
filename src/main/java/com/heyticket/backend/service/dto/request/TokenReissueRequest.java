package com.heyticket.backend.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TokenReissueRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String refreshToken;

}

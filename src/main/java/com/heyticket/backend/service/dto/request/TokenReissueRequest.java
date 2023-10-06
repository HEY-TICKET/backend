package com.heyticket.backend.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TokenReissueRequest {

    @NotBlank
    private Long id;

    @NotBlank
    private String refreshToken;
}

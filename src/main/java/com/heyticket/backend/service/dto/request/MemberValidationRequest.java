package com.heyticket.backend.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberValidationRequest {

    @NotBlank
    private String email;
}

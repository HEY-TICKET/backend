package com.heyticket.backend.service.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PasswordUpdateRequest {

    private String currentPassword;

    private String newPassword;
}

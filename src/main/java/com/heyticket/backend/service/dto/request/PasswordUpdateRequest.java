package com.heyticket.backend.service.dto.request;

import lombok.Getter;

@Getter
public class PasswordUpdateRequest {

    private String currentPassword;

    private String newPassword;
}

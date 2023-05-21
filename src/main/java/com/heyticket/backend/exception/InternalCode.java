package com.heyticket.backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InternalCode {

    OK("OK"),
    CREATED("CREATED"),
    SERVER_ERROR("SERVER-ERROR"),

    INVALID_JWT("JWT-001"),
    EXPIRED_JWT("JWT-002");

    private final String name;

}

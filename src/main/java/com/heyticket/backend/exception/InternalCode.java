package com.heyticket.backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InternalCode {

    OK("OK"),
    CREATED("CREATED"),
    SERVER_ERROR("SERVER_ERROR"),
    NOT_FOUND("NOT_FOUND"),
    BAD_REQUEST("BAD_REQUEST"),

    INVALID_JWT("INVALID_JWT"),
    EXPIRED_JWT("EXPIRED_JWT"),
    USER_NOT_FOUND("USER_NOT_FOUND"),
    VALIDATION_FAILURE("VALIDATION_FAILURE");

    private final String name;
}

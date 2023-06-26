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

    EXISTING_EMAIL("EXISTING_EMAIL"),
    EXPIRED_CODE("EXPIRED_CODE"),
    INVALID_JWT("INVALID_JWT"),
    INVALID_PW("INVALID_PW"),
    INVALID_EMAIL("INVALID_EMAIL"),
    EXPIRED_JWT("EXPIRED_JWT"),
    USER_NOT_FOUND("USER_NOT_FOUND"),
    PW_MISMATCH("PW_MISMATCH"),
    VERIFICATION_FAILURE("VERIFICATION_FAILURE"),
    VALIDATION_FAILURE("VALIDATION_FAILURE");

    private final String name;
}

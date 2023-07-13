package com.heyticket.backend.exception;

import lombok.Getter;

@Getter
public class AuthenticationFailureException extends RuntimeException {

    private InternalCode code;

    public AuthenticationFailureException(String message, InternalCode code) {
        super(message);
        this.code = code;
    }

    public AuthenticationFailureException(String message) {
        super(message);
    }

    public AuthenticationFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationFailureException(Throwable cause) {
        super(cause);
    }
}

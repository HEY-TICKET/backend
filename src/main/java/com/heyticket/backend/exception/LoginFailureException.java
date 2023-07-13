package com.heyticket.backend.exception;

import lombok.Getter;

@Getter
public class LoginFailureException extends RuntimeException {

    private InternalCode code;

    public LoginFailureException(InternalCode code) {
        this.code = code;
    }

    public LoginFailureException(String message, InternalCode code) {
        super(message);
        this.code = code;
    }

    public LoginFailureException(String message, Throwable cause, InternalCode code) {
        super(message, cause);
        this.code = code;
    }

    public LoginFailureException(Throwable cause, InternalCode code) {
        super(cause);
        this.code = code;
    }

    public LoginFailureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, InternalCode code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }
}

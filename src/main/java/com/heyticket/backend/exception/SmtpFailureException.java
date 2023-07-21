package com.heyticket.backend.exception;

import lombok.Getter;

@Getter
public class SmtpFailureException extends RuntimeException {

    private InternalCode code;

    public SmtpFailureException(String message, InternalCode code) {
        super(message);
        this.code = code;
    }

    public SmtpFailureException(String message) {
        super(message);
    }

    public SmtpFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public SmtpFailureException(Throwable cause) {
        super(cause);
    }
}

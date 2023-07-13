package com.heyticket.backend.exception;

import com.heyticket.backend.service.dto.response.CommonResponse;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<?> runtimeExceptionHandler(RuntimeException e) {
        log.warn(e.getMessage());
        e.printStackTrace();
        return CommonResponse.serverError(InternalCode.SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(value = NoSuchElementException.class)
    public ResponseEntity<?> noSuchElementExceptionHandler(NoSuchElementException e) {
        log.warn(e.getMessage());
        e.printStackTrace();
        return CommonResponse.notFound(InternalCode.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(value = ValidationFailureException.class)
    public ResponseEntity<?> jwtValidationExceptionHandler(ValidationFailureException e) {
        log.warn(e.getMessage());
        e.printStackTrace();
        return CommonResponse.serverError(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<?> badCredentialExceptionHandler(BadCredentialsException e) {
        log.warn("Invalid user information.");
        e.printStackTrace();
        return CommonResponse.badRequest(InternalCode.INVALID_JWT, "Invalid user information.");
    }

    @ExceptionHandler(value = LoginFailureException.class)
    public ResponseEntity<?> loginFailureExceptionHandler(LoginFailureException e) {
        log.warn("Invalid user information.");
        e.printStackTrace();
        return CommonResponse.badRequest(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<?> notFoundExceptionHandler(NotFoundException e) {
        log.warn("Not found Exception.");
        e.printStackTrace();
        return CommonResponse.badRequest(e.getCode(), e.getMessage());
    }
}

package com.heyticket.backend.exception;

import com.heyticket.backend.service.dto.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<?> runtimeExceptionHandler(Exception e) {
        log.warn(e.getMessage());
        e.printStackTrace();
        return CommonResponse.serverError(InternalCode.SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(value = JwtValidationException.class)
    public ResponseEntity<?> jwtValidationExceptionHandler(JwtValidationException e) {
        log.warn(e.getMessage());
        e.printStackTrace();
        return CommonResponse.serverError(e.getCode(), e.getMessage());
    }
}

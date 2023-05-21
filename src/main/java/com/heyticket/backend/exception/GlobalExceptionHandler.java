package com.heyticket.backend.exception;

import com.heyticket.backend.service.dto.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<?> runtimeExceptionHandler(Exception e) {
        return CommonResponse.serverError(InternalCode.SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(value = JwtValidationException.class)
    public ResponseEntity<?> jwtValidationExceptionHandler(JwtValidationException e) {
        return CommonResponse.serverError(e.getCode(), e.getMessage());
    }

}

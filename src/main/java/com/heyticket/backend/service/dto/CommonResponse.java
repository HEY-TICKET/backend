package com.heyticket.backend.service.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class CommonResponse<T> extends ResponseEntity<T> {

    private int code;

    private String message;

    private T data;

    public static <T> CommonResponse<T> ok(String message, T data) {
        return new CommonResponse<>(HttpStatus.OK, message, data);
    }

    public CommonResponse(HttpStatusCode code, String message, T data) {
        super(code);
        this.code = code.value();
        this.message = message;
        this.data = data;
    }

}

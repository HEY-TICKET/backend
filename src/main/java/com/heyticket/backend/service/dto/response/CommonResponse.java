package com.heyticket.backend.service.dto.response;

import com.heyticket.backend.exception.InternalCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
public class CommonResponse<T> {

    private String code;

    private String message;

    private T data;

    public static <T> ResponseEntity<CommonResponse<?>> ok(String message, T data) {
        return new ResponseEntity<>(new CommonResponse<>(InternalCode.OK, message, data), HttpStatus.OK);
    }

    public static <T> ResponseEntity<CommonResponse<?>> serverError(InternalCode code, String message) {
        return new ResponseEntity<>(new CommonResponse<>(code, message), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static <T> ResponseEntity<CommonResponse<?>> badRequest(InternalCode code, String message) {
        return new ResponseEntity<>(new CommonResponse<>(code, message), HttpStatus.BAD_REQUEST);
    }

    public static <T> ResponseEntity<CommonResponse<?>> notFound(InternalCode code, String message) {
        return new ResponseEntity<>(new CommonResponse<>(code, message), HttpStatus.NOT_FOUND);
    }

    public CommonResponse(InternalCode code, String message, T data) {
        this.code = code.getName();
        this.message = message;
        this.data = data;
    }

    public CommonResponse(InternalCode code, String message) {
        this.code = code.getName();
        this.message = message;
    }
}

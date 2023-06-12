package com.heyticket.backend.service.dto.swaggerresponse;

import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.service.dto.response.CommonResponse;

public class StringCommonResponse extends CommonResponse<String> {

    public StringCommonResponse(InternalCode code, String message, String data) {
        super(code, message, data);
    }

    public StringCommonResponse(InternalCode code, String message) {
        super(code, message);
    }
}

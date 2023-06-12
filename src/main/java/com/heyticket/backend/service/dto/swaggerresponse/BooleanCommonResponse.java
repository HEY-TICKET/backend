package com.heyticket.backend.service.dto.swaggerresponse;

import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.service.dto.response.CommonResponse;

public class BooleanCommonResponse extends CommonResponse<Boolean> {

    public BooleanCommonResponse(InternalCode code, String message, Boolean data) {
        super(code, message, data);
    }

    public BooleanCommonResponse(InternalCode code, String message) {
        super(code, message);
    }
}

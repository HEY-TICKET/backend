package com.heyticket.backend.service.dto.swaggerresponse;

import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.service.dto.response.CommonResponse;

public class LongCommonResponse extends CommonResponse<Long> {

    public LongCommonResponse(InternalCode code, String message, Long data) {
        super(code, message, data);
    }

    public LongCommonResponse(InternalCode code, String message) {
        super(code, message);
    }
}

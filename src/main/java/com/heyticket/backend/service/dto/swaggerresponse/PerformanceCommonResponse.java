package com.heyticket.backend.service.dto.swaggerresponse;

import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.service.dto.response.CommonResponse;
import com.heyticket.backend.service.dto.response.PerformanceResponse;

public class PerformanceCommonResponse extends CommonResponse<PerformanceResponse> {

    public PerformanceCommonResponse(InternalCode code, String message, PerformanceResponse data) {
        super(code, message, data);
    }

    public PerformanceCommonResponse(InternalCode code, String message) {
        super(code, message);
    }
}

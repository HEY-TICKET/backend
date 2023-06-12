package com.heyticket.backend.service.dto.swaggerresponse;

import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.service.dto.response.CommonResponse;
import com.heyticket.backend.service.dto.response.PerformanceResponse;
import java.util.List;

public class ListPerformanceCommonResponse extends CommonResponse<List<PerformanceResponse>> {

    public ListPerformanceCommonResponse(InternalCode code, String message, List<PerformanceResponse> data) {
        super(code, message, data);
    }

    public ListPerformanceCommonResponse(InternalCode code, String message) {
        super(code, message);
    }
}

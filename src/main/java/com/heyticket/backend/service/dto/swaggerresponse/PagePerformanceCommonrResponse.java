package com.heyticket.backend.service.dto.swaggerresponse;

import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.service.dto.pagable.PageResponse;
import com.heyticket.backend.service.dto.response.CommonResponse;
import com.heyticket.backend.service.dto.response.PerformanceResponse;

public class PagePerformanceCommonrResponse extends CommonResponse<PageResponse<PerformanceResponse>> {

    public PagePerformanceCommonrResponse(InternalCode code, String message, PageResponse<PerformanceResponse> data) {
        super(code, message, data);
    }

    public PagePerformanceCommonrResponse(InternalCode code, String message) {
        super(code, message);
    }
}

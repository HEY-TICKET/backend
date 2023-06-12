package com.heyticket.backend.service.dto.swaggerresponse;

import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.service.dto.pagable.PageResponse;
import com.heyticket.backend.service.dto.response.BoxOfficeRankResponse;
import com.heyticket.backend.service.dto.response.CommonResponse;

public class PageBoxOfficeRankCommonResponse extends CommonResponse<PageResponse<BoxOfficeRankResponse>> {

    public PageBoxOfficeRankCommonResponse(InternalCode code, String message, PageResponse<BoxOfficeRankResponse> data) {
        super(code, message, data);
    }

    public PageBoxOfficeRankCommonResponse(InternalCode code, String message) {
        super(code, message);
    }
}

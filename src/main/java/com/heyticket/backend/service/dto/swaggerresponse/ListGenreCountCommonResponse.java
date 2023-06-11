package com.heyticket.backend.service.dto.swaggerresponse;

import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.service.dto.response.CommonResponse;
import com.heyticket.backend.service.dto.response.GenreCountResponse;
import java.util.List;

public class ListGenreCountCommonResponse extends CommonResponse<List<GenreCountResponse>> {

    public ListGenreCountCommonResponse(InternalCode code, String message, List<GenreCountResponse> data) {
        super(code, message, data);
    }

    public ListGenreCountCommonResponse(InternalCode code, String message) {
        super(code, message);
    }
}

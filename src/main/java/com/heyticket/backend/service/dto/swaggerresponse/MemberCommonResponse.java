package com.heyticket.backend.service.dto.swaggerresponse;

import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.service.dto.response.CommonResponse;
import com.heyticket.backend.service.dto.response.MemberResponse;

public class MemberCommonResponse extends CommonResponse<MemberResponse> {

    public MemberCommonResponse(InternalCode code, String message, MemberResponse data) {
        super(code, message, data);
    }

    public MemberCommonResponse(InternalCode code, String message) {
        super(code, message);
    }
}

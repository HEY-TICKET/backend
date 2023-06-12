package com.heyticket.backend.service.dto.swaggerresponse;

import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.module.security.jwt.dto.TokenInfo;
import com.heyticket.backend.service.dto.response.CommonResponse;

public class TokenInfoCommonResponse extends CommonResponse<TokenInfo> {

    public TokenInfoCommonResponse(InternalCode code, String message, TokenInfo data) {
        super(code, message, data);
    }

    public TokenInfoCommonResponse(InternalCode code, String message) {
        super(code, message);
    }
}

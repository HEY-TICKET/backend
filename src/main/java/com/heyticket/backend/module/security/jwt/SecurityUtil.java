package com.heyticket.backend.module.security.jwt;

import com.heyticket.backend.exception.AuthenticationFailureException;
import com.heyticket.backend.exception.InternalCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static String getCurrentMemberEmail() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new AuthenticationFailureException("No authentication information.", InternalCode.NOT_FOUND);
        }
        return authentication.getName();
    }
}

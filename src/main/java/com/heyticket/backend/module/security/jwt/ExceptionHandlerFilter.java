package com.heyticket.backend.module.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.exception.JwtValidationException;
import com.heyticket.backend.service.dto.response.CommonResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("runtime exception exception handler filter");
            setErrorResponse(response, e);
        }
    }

    public void setErrorResponse(HttpServletResponse response, Throwable ex) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setContentType("application/json; charset=UTF-8");
        CommonResponse commonResponse;
        if (ex instanceof JwtValidationException jwtValidationException) {
            commonResponse = new CommonResponse<>(jwtValidationException.getCode(), jwtValidationException.getMessage());
        } else {
            commonResponse = new CommonResponse(InternalCode.SERVER_ERROR, ex.getMessage());
        }
        try {
            response.getWriter().write(objectMapper.writeValueAsString(commonResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
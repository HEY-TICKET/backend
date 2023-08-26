package com.heyticket.backend.service.dto;

import com.heyticket.backend.service.enums.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfo {

    private String email;

    private String password;

    private AuthProvider authProvider;
}

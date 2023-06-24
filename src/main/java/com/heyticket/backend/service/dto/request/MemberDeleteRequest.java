package com.heyticket.backend.service.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberDeleteRequest {

    private String email;

    private String password;
}

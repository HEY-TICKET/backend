package com.heyticket.backend.service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDeleteRequest {

    private String email;

    private String password;

}

package com.heyticket.backend.service.dto.request;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSignUpRequest {

    private String email;

    private String password;

    private String verificationCode;

    private List<String> genres = new ArrayList<>();

    private List<String> areas = new ArrayList<>();

    private List<String> keywords = new ArrayList<>();

}

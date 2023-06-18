package com.heyticket.backend.service.dto.request;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberSignUpRequest {

    private String email;

    private String password;

    private String verificationCode;

    @Builder.Default
    private List<String> genres = new ArrayList<>();

    @Builder.Default
    private List<String> areas = new ArrayList<>();

    @Builder.Default
    private List<String> keywords = new ArrayList<>();

    private boolean keywordPush;
}

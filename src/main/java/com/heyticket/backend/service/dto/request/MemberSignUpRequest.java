package com.heyticket.backend.service.dto.request;

import com.heyticket.backend.service.enums.Area;
import com.heyticket.backend.service.enums.Genre;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignUpRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String verificationCode;

    @Builder.Default
    private List<Genre> genres = new ArrayList<>();

    @Builder.Default
    private List<Area> areas = new ArrayList<>();

    @Builder.Default
    private List<String> keywords = new ArrayList<>();

    @NotBlank
    private boolean keywordPush;
}

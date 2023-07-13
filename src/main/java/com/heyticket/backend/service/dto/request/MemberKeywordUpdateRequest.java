package com.heyticket.backend.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberKeywordUpdateRequest {

    @NotBlank
    private String email;

    @Builder.Default
    private List<String> keywords = new ArrayList<>();
}

package com.heyticket.backend.service.dto.request;

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
public class MemberCategoryUpdateRequest {

    private String email;

    private List<String> genres = new ArrayList<>();

    private List<String> areas = new ArrayList<>();
}

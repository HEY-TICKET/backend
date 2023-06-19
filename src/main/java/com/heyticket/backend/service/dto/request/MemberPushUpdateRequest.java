package com.heyticket.backend.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberPushUpdateRequest {

    private String email;

    private boolean isPushEnabled;
}

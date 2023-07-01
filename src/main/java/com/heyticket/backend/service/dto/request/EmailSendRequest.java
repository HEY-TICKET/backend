package com.heyticket.backend.service.dto.request;

import com.heyticket.backend.service.enums.VerificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailSendRequest {

    @NotBlank
    private String email;

    @NotNull
    private VerificationType verificationType;

}

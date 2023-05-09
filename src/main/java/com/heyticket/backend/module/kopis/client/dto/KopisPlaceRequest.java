package com.heyticket.backend.module.kopis.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KopisPlaceRequest {

    private int cpage;

    private int rows;

}

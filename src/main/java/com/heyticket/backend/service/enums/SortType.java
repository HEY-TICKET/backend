package com.heyticket.backend.service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortType {

    CREATED_DATE,
    VIEW_COUNT,
    END_DATE
}

package com.heyticket.backend.service.dto.pagable;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> contents;

    private Integer page;

    private Integer pageSize;

    private Integer totalPages;
}

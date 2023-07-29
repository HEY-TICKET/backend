package com.heyticket.backend.service.dto.pagable;

import java.util.List;
import lombok.Getter;

@Getter
public class PageResponse<T> {

    private List<T> contents;

    private Integer page;

    private Integer pageSize;

    private Integer totalPages;

    private Integer totalHits;

    public PageResponse(List<T> contents, Integer page, Integer pageSize, Integer totalPages) {
        this.contents = contents;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
    }

    public PageResponse(List<T> contents, Integer page, Integer pageSize, Integer totalPages, Integer totalHits) {
        this.contents = contents;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalHits = totalHits;
    }
}

package com.heyticket.backend.service.dto.pagable;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@AllArgsConstructor
@Getter
public class PageResponse<T> {

    private List<T> contents;

    private Integer page;

    private Integer pageSize;

    private Integer totalPages;

    public PageResponse(List<T> contents, Pageable pageable, Integer totalPages) {
        this.contents = contents;
        this.page = pageable.getPageNumber() + 1;
        this.pageSize = pageable.getPageSize();
        this.totalPages = totalPages;
    }
}

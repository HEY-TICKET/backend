package com.heyticket.backend.service.dto.pagable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.ObjectUtils;

@Getter
@AllArgsConstructor
public class CustomPageRequest {

    private static final int MAX_PAGE_SIZE = 50;

    private int page;

    private int pageSize;

    public void setPage(int page) {
        this.page = (ObjectUtils.isEmpty(page) || page <= 0) ? 1 : page;
    }

    public int getPage() {
        return page <= 0 ? 1 : page;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = (ObjectUtils.isEmpty(pageSize) || pageSize <= 0 || pageSize > MAX_PAGE_SIZE) ? MAX_PAGE_SIZE : pageSize;
    }

    public int getPageSize() {
        return (pageSize <= 0 || pageSize > MAX_PAGE_SIZE) ? MAX_PAGE_SIZE : pageSize;
    }

    public PageRequest of() {
        return PageRequest.of(getPage() - 1, getPageSize());
    }
}

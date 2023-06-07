package com.heyticket.backend.service.dto.request;

import com.heyticket.backend.domain.enums.PerformanceStatus;
import com.heyticket.backend.module.kopis.enums.SortOrder;
import com.heyticket.backend.module.kopis.enums.SortType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberLikeListRequest {

    private String email;

    private PerformanceStatus status;

    private SortType sortType = SortType.LIKE_DATE;

    private SortOrder sortOrder = SortOrder.DESC;
}

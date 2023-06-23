package com.heyticket.backend.service.dto.request;

import com.heyticket.backend.domain.enums.PerformanceStatus;
import com.heyticket.backend.service.enums.LikeSortType;
import com.heyticket.backend.service.enums.SortOrder;
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

    @Builder.Default
    private LikeSortType likeSortType = LikeSortType.LIKE_DATE;

    @Builder.Default
    private SortOrder sortOrder = SortOrder.DESC;
}

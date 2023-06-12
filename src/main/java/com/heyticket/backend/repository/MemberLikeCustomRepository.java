package com.heyticket.backend.repository;

import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.service.dto.request.MemberLikeListRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberLikeCustomRepository {

    Page<Performance> findMemberLikePerformanceByMemberEmail(MemberLikeListRequest request, Pageable pageable);
}

package com.heyticket.backend.repository;

import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.service.dto.request.NewPerformanceRequest;
import com.heyticket.backend.service.dto.request.PerformanceFilterRequest;
import com.heyticket.backend.service.dto.request.PerformanceSearchRequest;
import com.heyticket.backend.service.dto.response.GenreCountResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PerformanceCustomRepository {

    List<String> findAllIds();

    Page<Performance> findNewPerformances(NewPerformanceRequest newPerformanceRequest, Pageable pageable);

    List<GenreCountResponse> findPerformanceGenreCount();

    Page<Performance> findPerformanceByCondition(PerformanceFilterRequest request, Pageable pageable);

    Page<Performance> findPerformanceBySearchQuery(PerformanceSearchRequest request, Pageable pageable);
}

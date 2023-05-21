package com.heyticket.backend.repository;

import com.heyticket.backend.domain.Performance;
import com.heyticket.backend.service.dto.request.NewPerformanceRequest;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PerformanceCustomRepository {

    List<String> findAllIds();

    Page<Performance> findNewPerformances(NewPerformanceRequest newPerformanceRequest, Pageable pageable);

}

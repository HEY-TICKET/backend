package com.heyticket.backend.repository;

import com.heyticket.backend.kopis.domain.Performance;
import java.util.List;
import java.util.Optional;

public interface PerformanceCustomRepository {

    Optional<Performance> findById(String id);

    List<String> findAllIds();

    List<Performance> findNewPerformances();

}

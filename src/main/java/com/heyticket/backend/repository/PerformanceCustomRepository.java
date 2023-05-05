package com.heyticket.backend.repository;

import com.heyticket.backend.kopis.domain.Performance;
import java.util.Optional;

public interface PerformanceCustomRepository {

    Optional<Performance> getById(String id);

}

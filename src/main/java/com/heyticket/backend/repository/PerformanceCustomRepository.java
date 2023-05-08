package com.heyticket.backend.repository;

import com.heyticket.backend.domain.Performance;
import java.util.List;

public interface PerformanceCustomRepository {

    List<String> findAllIds();

    List<Performance> findNewPerformances();

}

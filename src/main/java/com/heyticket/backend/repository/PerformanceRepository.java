package com.heyticket.backend.repository;

import com.heyticket.backend.performances.domain.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {

}

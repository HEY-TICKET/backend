package com.heyticket.backend.repository;

import com.heyticket.backend.kopis.domain.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceRepository extends JpaRepository<Performance, String> {

}

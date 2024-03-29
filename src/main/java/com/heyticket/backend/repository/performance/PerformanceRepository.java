package com.heyticket.backend.repository.performance;

import com.heyticket.backend.domain.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, String>, PerformanceCustomRepository {

}

package com.heyticket.backend.repository;

import com.heyticket.backend.kopis.domain.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, String>, PerformanceCustomRepository {

}

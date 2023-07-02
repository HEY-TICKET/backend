package com.heyticket.backend.repository.performance;

import com.heyticket.backend.domain.PerformancePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformancePriceRepository extends JpaRepository<PerformancePrice, Long> {

}

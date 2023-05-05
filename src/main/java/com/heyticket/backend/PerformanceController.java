package com.heyticket.backend;

import com.heyticket.backend.service.PerformanceService;
import com.heyticket.backend.service.dto.PerformanceDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;

    @GetMapping("/performances/new")
    public List<PerformanceDto> getNewPerformances() {
        return performanceService.getNewPerformances();
    }

}

package com.heyticket.backend.controller;

import com.heyticket.backend.module.kopis.client.dto.BoxOfficeResponse;
import com.heyticket.backend.service.PerformanceService;
import com.heyticket.backend.service.dto.BoxOfficeRequest;
import com.heyticket.backend.service.dto.PerformanceDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;

    @GetMapping("/performances/new")
    public List<PerformanceDto> getNewPerformances() {
        return performanceService.getNewPerformances();
    }

    @GetMapping("/performances/boxoffice/uni")
    public List<BoxOfficeResponse> getUniBoxOffice() {
        return performanceService.getUniBoxOffice();
    }

    @GetMapping("/performances/boxoffice")
    public List<BoxOfficeResponse> getBoxOffice(@RequestBody BoxOfficeRequest request) {
        return performanceService.getBoxOffice(request);
    }

}

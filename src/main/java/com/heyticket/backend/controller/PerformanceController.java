package com.heyticket.backend.controller;

import com.heyticket.backend.module.kopis.client.dto.KopisBoxOfficeResponse;
import com.heyticket.backend.service.PerformanceService;
import com.heyticket.backend.service.dto.BoxOfficeRequest;
import com.heyticket.backend.service.dto.CommonResponse;
import com.heyticket.backend.service.dto.PerformanceDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;

    @GetMapping("/performances/new")
    public CommonResponse<List<PerformanceDto>> getNewPerformances() {
        return performanceService.getNewPerformances();
    }

    @GetMapping("/performances/boxoffice/uni")
    public ResponseEntity<List<KopisBoxOfficeResponse>> getUniBoxOffice() {
        return performanceService.getUniBoxOffice();
    }

    @GetMapping("/performances/boxoffice")
    public ResponseEntity<List<KopisBoxOfficeResponse>> getBoxOffice(BoxOfficeRequest request) {
        return performanceService.getBoxOffice(request);
    }

    @GetMapping("/performances/{id}")
    public PerformanceDto getPerformance(@PathVariable String id) {
        return performanceService.getPerformanceById(id);
    }

}

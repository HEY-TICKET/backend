package com.heyticket.backend.controller;

import com.heyticket.backend.service.PerformanceService;
import com.heyticket.backend.service.PlaceService;
import com.heyticket.backend.service.dto.request.PerformanceBatchUpdateRequest;
import com.heyticket.backend.service.dto.response.CommonResponse;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/batch")
@Hidden
public class BatchController {

    private final PerformanceService performanceService;

    private final PlaceService placeService;

    @GetMapping("/performances")
    public ResponseEntity<?> updatePerformancesBatch(@Valid PerformanceBatchUpdateRequest request) {
        int count = performanceService.updatePerformancesBatch(request.getFrom(), request.getTo(), request.getRows());
        return CommonResponse.ok("Success in batch updating performances. Updated performances count in data", count);
    }

    @GetMapping("/performances/rank")
    public ResponseEntity<?> updateBoxOfficeRankBatch() {
        int count = performanceService.updateBoxOfficeRankBatch();
        return CommonResponse.ok("Success in batch updating performance rank. Updated performances count in data", count);
    }

    @GetMapping("/performances/status")
    public ResponseEntity<?> updatePerformanceStatusBatch() {
        int count = performanceService.updatePerformanceStatusBatch();
        return CommonResponse.ok("Success in batch updating performance status. Updated performances count in data", count);
    }

    @GetMapping("/places")
    public ResponseEntity<?> updatePlacesBatch() {
        int count = placeService.updatePlacesBatch();
        return CommonResponse.ok("Success in batch updating places. Updated performances count in data", count);
    }

    @GetMapping("/performances/search")
    public ResponseEntity<?> updatePerformanceSearch() {
        performanceService.updatePerformanceMeiliData();
        return CommonResponse.ok("Success in batch updating performance search data.", true);
    }
}

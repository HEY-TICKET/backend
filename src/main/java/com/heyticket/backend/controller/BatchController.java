package com.heyticket.backend.controller;

import com.heyticket.backend.service.PerformanceService;
import com.heyticket.backend.service.PlaceService;
import com.heyticket.backend.service.dto.request.PerformanceBatchUpdateRequest;
import com.heyticket.backend.service.dto.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/batch")
public class BatchController {

    private final PerformanceService performanceService;

    private final PlaceService placeService;

    @GetMapping("/performances")
    public ResponseEntity<CommonResponse> updatePerformancesBatch(PerformanceBatchUpdateRequest request) {
        int count = performanceService.updatePerformancesBatch(request.getFrom(), request.getTo(), request.getRows());
        return CommonResponse.ok("Success to performance batch update. Updated performances count in data", count);
    }

    @GetMapping("/performances/rank")
    public ResponseEntity<CommonResponse> updateBoxOfficeRankBatch() {
        int count = performanceService.updateBoxOfficeRankBatch();
        return CommonResponse.ok("Success to performance rank batch update. Updated performances count in data", count);
    }

    @GetMapping("/performances/status")
    public ResponseEntity<CommonResponse> updatePerformanceStatusBatch() {
        int count = performanceService.updatePerformanceStatusBatch();
        return CommonResponse.ok("Success to performance status batch update. Updated performances count in data", count);
    }

    @GetMapping("/places")
    public ResponseEntity<CommonResponse> updatePlacesBatch() {
        int count = placeService.updatePlacesBatch();
        return CommonResponse.ok("Success to place batch update. Updated performances count in data", count);
    }
}

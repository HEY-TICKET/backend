package com.heyticket.backend.controller;

import com.heyticket.backend.module.kopis.client.dto.KopisBoxOfficeResponse;
import com.heyticket.backend.service.PerformanceService;
import com.heyticket.backend.service.dto.BoxOfficeRankRequest;
import com.heyticket.backend.service.dto.BoxOfficeRankResponse;
import com.heyticket.backend.service.dto.NewPerformanceRequest;
import com.heyticket.backend.service.dto.PerformanceResponse;
import com.heyticket.backend.service.dto.pagable.PageRequest;
import com.heyticket.backend.service.dto.pagable.PageResponse;
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
    public PageResponse<PerformanceResponse> getNewPerformances(NewPerformanceRequest request, PageRequest pageRequest) {
        return performanceService.getNewPerformances(request, pageRequest.of());
    }

    @GetMapping("/performances/boxoffice/uni")
    public ResponseEntity<List<KopisBoxOfficeResponse>> getUniBoxOffice() {
        return performanceService.getUniBoxOffice();
    }

    @GetMapping("/performances/boxoffice")
    public PageResponse<BoxOfficeRankResponse> getBoxOffice(BoxOfficeRankRequest request, PageRequest pageRequest) {
        return performanceService.getBoxOfficeRank(request, pageRequest.of());
    }

    @GetMapping("/performances/{id}")
    public PerformanceResponse getPerformance(@PathVariable String id) {
        return performanceService.getPerformanceById(id);
    }

}

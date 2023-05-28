package com.heyticket.backend.controller;

import com.heyticket.backend.service.PerformanceService;
import com.heyticket.backend.service.dto.request.BoxOfficeRankRequest;
import com.heyticket.backend.service.dto.response.BoxOfficeRankResponse;
import com.heyticket.backend.service.dto.request.NewPerformanceRequest;
import com.heyticket.backend.service.dto.response.CommonResponse;
import com.heyticket.backend.service.dto.response.GenreCountResponse;
import com.heyticket.backend.service.dto.response.PerformanceResponse;
import com.heyticket.backend.service.dto.pagable.PageRequest;
import com.heyticket.backend.service.dto.pagable.PageResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("rawtypes")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PerformanceController {

    private final PerformanceService performanceService;

    @GetMapping("/performances/new")
    public ResponseEntity<CommonResponse> getNewPerformances(NewPerformanceRequest request, PageRequest pageRequest) {
        PageResponse<PerformanceResponse> newPerformances = performanceService.getNewPerformances(request, pageRequest.of());
        return CommonResponse.ok("New performances", newPerformances);
    }

    @GetMapping("/performances/rank")
    public ResponseEntity<CommonResponse> getBoxOffice(BoxOfficeRankRequest request, PageRequest pageRequest) {
        PageResponse<BoxOfficeRankResponse> boxOfficeRank = performanceService.getBoxOfficeRank(request, pageRequest.of());
        return CommonResponse.ok("Performance rank.", boxOfficeRank);
    }

    @GetMapping("/performances/{id}")
    public ResponseEntity<CommonResponse> getPerformance(@PathVariable String id) {
        PerformanceResponse performanceResponse = performanceService.getPerformanceById(id);
        return CommonResponse.ok("Performance information.", performanceResponse);
    }

    @GetMapping("/performances/{id}/recommendation")
    public ResponseEntity<CommonResponse> getPerformanceRecommendation(@PathVariable String id) {
        List<PerformanceResponse> performanceRecommendation = performanceService.getPerformanceRecommendation(id);
        return CommonResponse.ok("Performance recommendation.", performanceRecommendation);
    }

    @GetMapping("/performances/genres/count")
    public ResponseEntity<CommonResponse> getPerformanceGenreCount() {
        List<GenreCountResponse> performanceGenreCount = performanceService.getPerformanceGenreCount();
        return CommonResponse.ok("Performance genre count.", performanceGenreCount);
    }
}

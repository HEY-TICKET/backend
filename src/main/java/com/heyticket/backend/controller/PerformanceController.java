package com.heyticket.backend.controller;

import com.heyticket.backend.service.PerformanceService;
import com.heyticket.backend.service.dto.pagable.CustomPageRequest;
import com.heyticket.backend.service.dto.pagable.PageResponse;
import com.heyticket.backend.service.dto.request.BoxOfficeRankRequest;
import com.heyticket.backend.service.dto.request.NewPerformanceRequest;
import com.heyticket.backend.service.dto.response.BoxOfficeRankResponse;
import com.heyticket.backend.service.dto.response.CommonResponse;
import com.heyticket.backend.service.dto.response.GenreCountResponse;
import com.heyticket.backend.service.dto.response.PerformanceResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PerformanceController {

    private final PerformanceService performanceService;

    @GetMapping("/performances/new")
    public ResponseEntity<?> getNewPerformances(NewPerformanceRequest request, CustomPageRequest customPageRequest) {
        PageResponse<PerformanceResponse> newPerformances = performanceService.getNewPerformances(request, customPageRequest.of());
        return CommonResponse.ok("New performances", newPerformances);
    }

    @GetMapping("/performances/rank")
    public ResponseEntity<?> getBoxOffice(BoxOfficeRankRequest request, CustomPageRequest customPageRequest) {
        PageResponse<BoxOfficeRankResponse> boxOfficeRank = performanceService.getBoxOfficeRank(request, customPageRequest.of());
        return CommonResponse.ok("Performance rank.", boxOfficeRank);
    }

    @GetMapping("/performances/{id}")
    public ResponseEntity<?> getPerformance(@PathVariable String id) {
        PerformanceResponse performanceResponse = performanceService.getPerformanceById(id);
        return CommonResponse.ok("Performance information.", performanceResponse);
    }

    @GetMapping("/performances/{id}/recommendation")
    public ResponseEntity<?> getPerformanceRecommendation(@PathVariable String id) {
        List<PerformanceResponse> performanceRecommendation = performanceService.getPerformanceRecommendation(id);
        return CommonResponse.ok("Performance recommendation.", performanceRecommendation);
    }

    @GetMapping("/performances/genres/count")
    public ResponseEntity<?> getPerformanceGenreCount() {
        List<GenreCountResponse> performanceGenreCount = performanceService.getPerformanceGenreCount();
        return CommonResponse.ok("Performance genre count.", performanceGenreCount);
    }
}

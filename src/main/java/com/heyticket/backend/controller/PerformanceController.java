package com.heyticket.backend.controller;

import com.heyticket.backend.service.PerformanceService;
import com.heyticket.backend.service.dto.pagable.CustomPageRequest;
import com.heyticket.backend.service.dto.pagable.PageResponse;
import com.heyticket.backend.service.dto.request.BoxOfficeRankRequest;
import com.heyticket.backend.service.dto.request.NewPerformanceRequest;
import com.heyticket.backend.service.dto.request.PerformanceFilterRequest;
import com.heyticket.backend.service.dto.request.PerformanceSearchRequest;
import com.heyticket.backend.service.dto.response.BoxOfficeRankResponse;
import com.heyticket.backend.service.dto.response.CommonResponse;
import com.heyticket.backend.service.dto.response.GenreCountResponse;
import com.heyticket.backend.service.dto.response.PerformanceResponse;
import com.heyticket.backend.service.dto.swaggerresponse.ListGenreCountCommonResponse;
import com.heyticket.backend.service.dto.swaggerresponse.ListPerformanceCommonResponse;
import com.heyticket.backend.service.dto.swaggerresponse.PageBoxOfficeRankCommonResponse;
import com.heyticket.backend.service.dto.swaggerresponse.PagePerformanceCommonrResponse;
import com.heyticket.backend.service.dto.swaggerresponse.PerformanceCommonResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = PagePerformanceCommonrResponse.class)))})
    @GetMapping("/performances")
    public ResponseEntity<?> getPerformancesByCondition(PerformanceFilterRequest request, CustomPageRequest customPageRequest) {
        PageResponse<PerformanceResponse> filteredPerformances = performanceService.getPerformancesByCondition(request, customPageRequest.of());
        return CommonResponse.ok("Filtered performances", filteredPerformances);
    }

    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = PagePerformanceCommonrResponse.class)))})
    @GetMapping("/performances/search")
    public ResponseEntity<?> searchPerformances(PerformanceSearchRequest request, CustomPageRequest customPageRequest) {
        PageResponse<PerformanceResponse> searchedPerformances = performanceService.searchPerformances(request, customPageRequest.of());
        return CommonResponse.ok("Search query result.", searchedPerformances);
    }

    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = PagePerformanceCommonrResponse.class)))})
    @GetMapping("/performances/new")
    public ResponseEntity<?> getNewPerformances(NewPerformanceRequest request, CustomPageRequest customPageRequest) {
        PageResponse<PerformanceResponse> newPerformances = performanceService.getNewPerformances(request, customPageRequest.of());
        return CommonResponse.ok("New performances", newPerformances);
    }

    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = PageBoxOfficeRankCommonResponse.class)))})
    @GetMapping("/performances/rank")
    public ResponseEntity<?> getBoxOffice(BoxOfficeRankRequest request, CustomPageRequest customPageRequest) {
        PageResponse<BoxOfficeRankResponse> boxOfficeRank = performanceService.getBoxOfficeRank(request, customPageRequest.of());
        return CommonResponse.ok("Performance rank.", boxOfficeRank);
    }

    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = PerformanceCommonResponse.class)))})
    @GetMapping("/performances/{id}")
    public ResponseEntity<?> getPerformance(@PathVariable String id) {
        PerformanceResponse performanceResponse = performanceService.getPerformanceById(id);
        return CommonResponse.ok("Performance information.", performanceResponse);
    }

    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = ListPerformanceCommonResponse.class)))})
    @GetMapping("/performances/{id}/recommendation")
    public ResponseEntity<?> getPerformanceRecommendation(@PathVariable String id) {
        List<PerformanceResponse> performanceRecommendation = performanceService.getPerformanceRecommendation(id);
        return CommonResponse.ok("Performance recommendation.", performanceRecommendation);
    }

    @ApiResponses(value = {@ApiResponse(content = @Content(schema = @Schema(implementation = ListGenreCountCommonResponse.class)))})
    @GetMapping("/performances/genres/count")
    public ResponseEntity<?> getPerformanceGenreCount() {
        List<GenreCountResponse> performanceGenreCount = performanceService.getPerformanceGenreCount();
        return CommonResponse.ok("Performance genre count.", performanceGenreCount);
    }
}

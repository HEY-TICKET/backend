package com.heyticket.backend.module.meilesearch.dto;

import com.heyticket.backend.service.dto.response.PerformanceResponse;
import com.heyticket.backend.service.enums.Genre;
import com.heyticket.backend.service.enums.PerformanceStatus;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeiliHilightedPerformanceResponse {

    private String id;
    private String title;
    private String cast;
    private LocalDate startDate;
    private LocalDate endDate;
    private String theater;
    private String runtime;
    private String age;
    private String price;
    private String poster;
    private Genre genre;
    private String status;
    private boolean openRun;
    private String schedule;
    private Integer views;
    private String company;
    private String address;
    private Double latitude;
    private Double longitude;
    private String phoneNumber;
    private String sido;
    private String gugun;

    public PerformanceResponse toPerformanceResponse() {
        return PerformanceResponse.builder()
            .id(this.id)
            .title(this.title)
            .startDate(this.startDate)
            .endDate(this.endDate)
            .theater(this.theater)
            .cast(this.cast)
            .runtime(this.runtime)
            .age(this.age)
            .company(this.company)
            .price(this.price)
            .poster(this.poster)
            .genre(this.genre)
            .status(PerformanceStatus.getByPriority(status.split(",")[0]))
            .openRun(this.openRun)
            .schedule(this.schedule)
            .views(this.views)
            .build();
    }
}

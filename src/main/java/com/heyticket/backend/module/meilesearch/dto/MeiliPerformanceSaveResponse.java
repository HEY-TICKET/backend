package com.heyticket.backend.module.meilesearch.dto;

import com.heyticket.backend.service.enums.Area;
import com.heyticket.backend.service.enums.Genre;
import com.heyticket.backend.service.enums.PerformanceStatus;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class MeiliPerformanceSaveResponse {

    private String id;
    private String address;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String theater;
    private String cast;
    private String runtime;
    private String age;
    private String company;
    private String price;
    private String poster;
    private Genre genre;
    private PerformanceStatus status;
    private boolean openRun;
    private String schedule;
    private Integer views;
    private Double latitude;
    private Double longitude;
    private String phoneNumber;
    private Area sido;
    private String gugun;

    public MeiliPerformanceDocument toStringForm() {
        return MeiliPerformanceDocument.builder()
            .id(this.id)
            .address(this.address)
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
            .status(this.status.getPriority() + "," + this.status.getName())
            .openRun(this.openRun)
            .schedule(this.schedule)
            .views(this.views)
            .latitude(this.latitude)
            .longitude(this.longitude)
            .phoneNumber(this.phoneNumber)
            .sido(this.sido.getName())
            .gugun(this.gugun)
            .build();
    }
}

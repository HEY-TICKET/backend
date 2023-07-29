package com.heyticket.backend.module.meilesearch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.heyticket.backend.service.enums.Genre;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeiliPerformanceResponse {

    private String title;
    private String cast;
    private String id;
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
    @JsonProperty("_formatted")
    private MeiliHilightedPerformanceResponse formattedResponse;
}

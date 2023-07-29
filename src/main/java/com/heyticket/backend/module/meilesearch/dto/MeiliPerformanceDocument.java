package com.heyticket.backend.module.meilesearch.dto;

import com.heyticket.backend.service.enums.Genre;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeiliPerformanceDocument {

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
    private String status;
    private boolean openRun;
    private String schedule;
    private Integer views;
    private Double latitude;
    private Double longitude;
    private String phoneNumber;
    private String sido;
    private String gugun;
}

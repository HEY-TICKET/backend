package com.heyticket.backend.domain;

import com.heyticket.backend.module.kopis.enums.BoxOfficeArea;
import com.heyticket.backend.module.kopis.enums.BoxOfficeGenre;
import com.heyticket.backend.module.kopis.enums.TimePeriod;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BoxOfficeRank extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private BoxOfficeGenre boxOfficeGenre;

    @Enumerated(value = EnumType.STRING)
    private BoxOfficeArea boxOfficeArea;

    @Enumerated(value = EnumType.STRING)
    private TimePeriod timePeriod;

    @Column(length = 1000)
    private String performanceIds;

}

package com.heyticket.backend.domain;

import com.heyticket.backend.module.kopis.enums.Genre;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MemberGenre extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(value = EnumType.STRING)
    private Genre genre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email")
    private Member member;

    public static MemberGenre of(Genre genre, Member member) {
        return new MemberGenre(genre, member);
    }

    public MemberGenre(Genre genre, Member member) {
        this.genre = genre;
        this.member = member;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemberGenre that = (MemberGenre) o;
        return genre == that.genre;
    }

    @Override
    public int hashCode() {
        return Objects.hash(genre);
    }
}

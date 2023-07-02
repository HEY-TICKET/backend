package com.heyticket.backend.repository.performance;

import com.heyticket.backend.domain.BoxOfficeRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoxOfficeRankRepository extends JpaRepository<BoxOfficeRank, Long>, BoxOfficeRankCustomRepository {

}

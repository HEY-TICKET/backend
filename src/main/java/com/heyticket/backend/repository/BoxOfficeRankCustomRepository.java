package com.heyticket.backend.repository;

import com.heyticket.backend.domain.BoxOfficeRank;
import com.heyticket.backend.service.dto.BoxOfficeRankRequest;
import java.util.Optional;

public interface BoxOfficeRankCustomRepository {

    Optional<BoxOfficeRank> findBoxOfficeRank(BoxOfficeRankRequest request);

}

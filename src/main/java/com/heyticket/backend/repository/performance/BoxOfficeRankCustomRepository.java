package com.heyticket.backend.repository.performance;

import com.heyticket.backend.domain.BoxOfficeRank;
import com.heyticket.backend.service.dto.request.BoxOfficeRankRequest;
import java.util.Optional;

public interface BoxOfficeRankCustomRepository {

    Optional<BoxOfficeRank> findBoxOfficeRank(BoxOfficeRankRequest request);

}

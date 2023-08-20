package com.heyticket.backend.repository.keyword;

import com.heyticket.backend.domain.Keyword;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    Optional<Keyword> findByContent(String content);
}

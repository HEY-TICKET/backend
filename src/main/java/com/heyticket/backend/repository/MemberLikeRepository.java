package com.heyticket.backend.repository;

import com.heyticket.backend.domain.Member;
import com.heyticket.backend.domain.MemberLike;
import com.heyticket.backend.domain.Performance;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberLikeRepository extends JpaRepository<MemberLike, Long> {

    Optional<MemberLike> findMemberLikeByMemberAndPerformance(Member member, Performance performance);

    void deleteByMemberAndPerformance(Member member, Performance performance);
}

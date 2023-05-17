package com.heyticket.backend.repository;

import com.heyticket.backend.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberId(String username);
}

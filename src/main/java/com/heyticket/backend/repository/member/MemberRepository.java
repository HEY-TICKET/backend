package com.heyticket.backend.repository.member;

import com.heyticket.backend.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);
}

package com.start.portfolio.repository;

import com.start.portfolio.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
	// 단 건 조회는 Opional 이 좋다.
	Optional<Member> findByLoginId(String loginId);
}

package com.start.portfolio.repository;

import com.start.portfolio.entity.CompanyMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyMemberRepository extends JpaRepository<CompanyMember, Long> {

	Optional<CompanyMember> findByLoginId(String loginId);
}


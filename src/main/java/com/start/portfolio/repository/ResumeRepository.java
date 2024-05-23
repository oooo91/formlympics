package com.start.portfolio.repository;

import com.start.portfolio.entity.Resume;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

	Optional<Resume> findByIdAndMemberId(Long resumeId, Long memberId);
}

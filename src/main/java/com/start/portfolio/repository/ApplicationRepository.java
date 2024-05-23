package com.start.portfolio.repository;

import com.start.portfolio.entity.Application;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
	List<Application> findAllByRecruitmentId(Long recruitmentId);
}

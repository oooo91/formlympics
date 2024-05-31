package com.start.portfolio.repository;

import com.start.portfolio.entity.Alarm;
import com.start.portfolio.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

	List<Alarm> findAllByUser(User user);
}

package com.start.portfolio.repository;

import com.start.portfolio.entity.OrderExceptionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderExceptionLogRepository extends JpaRepository<OrderExceptionLog, Long> {
}

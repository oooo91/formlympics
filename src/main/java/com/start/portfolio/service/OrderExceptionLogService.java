package com.start.portfolio.service;

import com.start.portfolio.entity.OrderExceptionLog;
import com.start.portfolio.repository.OrderExceptionLogRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderExceptionLogService {

	private final OrderExceptionLogRepository orderExceptionLogRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveLog(Long userId, String errorMessage) {
		OrderExceptionLog exceptionLog = OrderExceptionLog.builder()
			.userId(userId)
			.errorMessage(errorMessage)
			.timestamp(LocalDateTime.now())
			.build();

		orderExceptionLogRepository.save(exceptionLog);
	}
}
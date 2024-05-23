package com.start.portfolio.repository;

import com.start.portfolio.entity.Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface StockRepository extends JpaRepository<Stock, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE) // TODO 비관적 락 구현
	@Query("select s from Stock s where s.id = :id")
	Stock findByIdWithPessimisticLock(Long id);

	@Lock(LockModeType.OPTIMISTIC)
	@Query("select s from Stock s where s.id = :id") // TODO 낙관적 락 구현
	Stock findByIdWithOptimisticLock(Long id);
}

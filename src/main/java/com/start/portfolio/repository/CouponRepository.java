package com.start.portfolio.repository;

import com.start.portfolio.entity.Coupon;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select c from Coupon c where c.id = :id")
	Optional<Coupon> findByIdWithPessimisticLock(Long id);
}

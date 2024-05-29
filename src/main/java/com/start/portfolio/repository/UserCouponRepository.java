package com.start.portfolio.repository;

import com.start.portfolio.entity.UserCoupon;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

	Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);
	long countByCouponId(Long couponId);
}

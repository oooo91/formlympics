package com.start.portfolio.service;

import com.start.portfolio.entity.Coupon;
import com.start.portfolio.entity.User;
import com.start.portfolio.entity.UserCoupon;
import com.start.portfolio.exception.CouponOutOfStockException;
import com.start.portfolio.repository.CouponRepository;
import com.start.portfolio.repository.UserCouponRepository;
import com.start.portfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

	private final UserRepository userRepository;
	private final UserCouponRepository userCouponRepository;
	private final CouponRepository couponRepository;

	@Transactional
	public void getCoupon(Long userId, Long couponId) {

		userCouponRepository.findByUserIdAndCouponId(
			userId, couponId).ifPresent(userCoupon -> {
			throw new CouponOutOfStockException("이미 쿠폰이 발급되었습니다.");
		});

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

		Coupon coupon = couponRepository.findById(couponId)
			.orElseThrow(() -> new RuntimeException("존재하지 않는 쿠폰입니다."));

		Long issuedCouponCount = coupon.getIssuedCouponCount();
		//Long count = couponCountRepository.increment();
		log.info("race condition = {}", issuedCouponCount);

		if (issuedCouponCount >= coupon.getTotalCouponQuantity()) {
			return;
		}
		coupon.update();

		couponRepository.save(coupon);
		userCouponRepository.save(UserCoupon.builder()
			.user(user)
			.coupon(coupon)
			.build());
	}
}

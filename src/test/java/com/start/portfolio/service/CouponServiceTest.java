package com.start.portfolio.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.start.portfolio.entity.Coupon;
import com.start.portfolio.entity.UserCoupon;
import com.start.portfolio.repository.CouponRepository;
import com.start.portfolio.repository.UserCouponRepository;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@TestPropertySource(properties = "spring.config.location=classpath:application-test.yml" )
class CouponServiceTest {

	@Autowired
	private CouponService couponService;

	@Autowired
	CouponRepository couponRepository;

	@Autowired
	UserCouponRepository userCouponRepository;

	@Test
	@DisplayName("한 번만 응모")
	public void applySingleCoupon() {
		Coupon coupon = couponRepository.save(
			Coupon.builder()
				.couponName("백원쿠폰")
				.couponAmount(100L)
				.totalCouponQuantity(100L)
				.remainingCouponQuantity(100L)
				.build()
		);
		couponService.getCoupon(1L, coupon.getId());
		assertThat(coupon.getRemainingCouponQuantity()).isEqualTo(99L);
	}

	@Test
	@DisplayName("여러 번 응모")
	public void concurrentCouponApplications() throws InterruptedException {
		Coupon coupon = couponRepository.save(
			Coupon.builder()
				.couponName("백원쿠폰")
				.couponAmount(100L)
				.totalCouponQuantity(100L)
				.remainingCouponQuantity(100L)
				.build()
		);

		int threadCount = 1000;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch(threadCount); //CountDownLatch -> 다른 스레드 작업 기다림


		for (int i = 0; i < threadCount; i++) {
			final long index = i + 1; // effectively final 변수로 선언
			executorService.submit(() -> {
				try {
					couponService.getCoupon(index, coupon.getId());
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();

		long count = userCouponRepository.countByCouponId(coupon.getId());
		assertThat(count).isEqualTo(100);
	}
}
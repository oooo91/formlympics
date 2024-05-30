package com.start.portfolio.facade;

import com.start.portfolio.service.CouponService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockCouponFacade {

	private final RedissonClient redissonClient;
	private final CouponService couponService;

	public void getCoupon(Long userId, Long couponId) {
		RLock lock = redissonClient.getLock(couponId.toString());
		try {
			boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS); //락을 시도하는 최대 시간, 락의 만료 시간(락을 획득하면 1초 동안 유지
			if (!available) {
				log.warn("LOCK 획득 실패");
				return;
			}

			log.info("LOCK 획득");
			couponService.getCoupon(userId, couponId);

		} catch (InterruptedException e) {
			log.warn("LOCK 획득 실패 - 예외 발생: {}", e.getMessage());
			throw new RuntimeException(e);
		} catch (Exception e) {
			log.error("예상치 못한 에러 발생: {}", e.getMessage());
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
			log.info("LOCK 해제");
		}
	}
}

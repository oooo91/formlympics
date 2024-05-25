package com.start.portfolio.facade;

import com.start.portfolio.service.StockService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockStockFacade {

	private final RedissonClient redissonClient;
	private final StockService stockService;

	public void decrease(Long id, Long quantity) {
		RLock lock = redissonClient.getLock(id.toString());

		try {
			boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS); //락을 시도하는 최대 시간, 락의 만료 시간(락을 획득하면 1초 동안 유지
			if (!available) {
				log.warn("LOCK 획득 실패");
				return;
			}

			log.info("LOCK 획득");
			stockService.decrease(id, quantity);
		} catch (InterruptedException e) {
			log.warn("LOCK 획득 실패 - 예외 발생: {}", e.getMessage());
		} catch (Exception e) {
			log.error("예상치 못한 예외 발생: {}", e.getMessage());
		} finally {
			lock.unlock();
			log.info("LOCK 해제");
		}
	}
}

package com.start.portfolio.facade;

import com.start.portfolio.repository.RedisLockRepository;
import com.start.portfolio.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LettuceLockStockFacade {

	private final RedisLockRepository redisLockRepository;
	private final StockService stockService;

	public void decrease(Long id, Long quantity) throws InterruptedException {
		// TODO -> Lock 획득하기 전까지 (락 획득 실패 시) Thread sleep 으로 텀을 두고 락 획득 재시도
		while (!redisLockRepository.lock(id)) {
			Thread.sleep(1000);
		}
		//TODO -> 락 획득 성공 시 재고 감소
		try {
			stockService.decrease(id, quantity);
		} finally {
			// TODO -> UNLOCK 메서드로 락 해제
			redisLockRepository.unlock(id);
		}
	}
}

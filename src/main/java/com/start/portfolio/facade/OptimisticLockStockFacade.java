package com.start.portfolio.facade;

import com.start.portfolio.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OptimisticLockStockFacade {

	private final StockService stockService;

	public void decrease(Long id, Long quantity) throws InterruptedException {
		while (true) {
			try {
				stockService.decrease(id, quantity);
				break; // TODO 정상 업데이트 시 -> while 문 빠져나옴
			} catch (Exception e) {
				Thread.sleep(50); // TODO 수량 감소 실패 -> 50ms 이후 재시도
			}
		}
	}
}

package com.start.portfolio.service;

import com.start.portfolio.entity.Stock;
import com.start.portfolio.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

	private final StockRepository stockRepository;

	@Transactional
	public void decrease(Long id, Long quantity) {
		//TODO 재고 조회
		Stock stock = stockRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("재고가 없습니다."));

		//TODO 재고 감소
		stock.decrease(quantity);

		//TODO 갱신된 값 저장
		stockRepository.saveAndFlush(stock);
	}

	@Transactional
	public void decrease_pess(Long id, Long quantity) {
		Stock stock = stockRepository.findByIdWithPessimisticLock(id);

		stock.decrease(quantity);

		stockRepository.saveAndFlush(stock);
	}

	@Transactional
	public void decrease_opti(Long id, Long quantity) {
		Stock stock = stockRepository.findByIdWithOptimisticLock(id);

		stock.decrease(quantity);

		stockRepository.saveAndFlush(stock);
	}
}

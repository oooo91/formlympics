package com.start.portfolio.service;

import com.start.portfolio.entity.Product;
import com.start.portfolio.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

	private final ProductRepository productRepository;

	@Transactional
	public void decrease(Long id, Long quantity) {
		//TODO 재고 조회
		Product product = productRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("재고가 없습니다."));

		//TODO 재고 감소
		product.decrease(quantity);

		//TODO 갱신된 값 저장
		productRepository.saveAndFlush(product);
	}
}

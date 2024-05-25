package com.start.portfolio.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.start.portfolio.entity.Product;
import com.start.portfolio.facade.LettuceLockStockFacade;
import com.start.portfolio.facade.OptimisticLockStockFacade;
import com.start.portfolio.facade.RedissonLockStockFacade;
import com.start.portfolio.repository.ProductRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class StockServiceTest {

	@Autowired
	private StockService stockService;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private OptimisticLockStockFacade optimisticLockStockFacade;
	@Autowired
	private RedissonLockStockFacade redissonLockStockFacade;

	@AfterEach
	public void after() {
		productRepository.deleteAll();
	}

	@Test
	@DisplayName("재고 감소 - 1개의 요청")
	public void decreaseStock() {
		stockService.decrease(1L, 1L); // TODO 수량 1 감소

		Product product = productRepository.findById(1L).orElseThrow();

		assertEquals(99, product.getStock());
	}

	@Test
	@DisplayName("재고 감소 - 100개의 요청 (sync, optimistic, pessimistic")
	public void decreaseStock_100() throws InterruptedException {
		int count = 100;
		long startTime = System.currentTimeMillis();

		ExecutorService executorService = Executors.newFixedThreadPool(32); // TODO 별도의 스레드 풀 생성 -> 최대 동시 요청 32개 허용
		CountDownLatch latch = new CountDownLatch(count);

		for (int i = 0; i < count; i++) {
			executorService.submit(() -> {
				try {
					optimisticLockStockFacade.decrease(1L, 1L); // TODO 수량 1 감소
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		Product product = productRepository.findById(1L).orElseThrow();
		assertEquals(0, product.getStock()); // 100 - (1 * 100) = 0

		log.info("테스트 진행 시간 : {} 초", (System.currentTimeMillis() - startTime) / 1000.0);
	}

	@Test
	@DisplayName("재고 감소 - 1개의 요청 (redis - lettuce, redisson")
	public void decreaseStock_100_2() throws InterruptedException {
		int count = 100;
		long startTime = System.currentTimeMillis();

		Product product = productRepository.saveAndFlush(Product.builder()
			.stock(100L)
			.build());

		ExecutorService executorService = Executors.newFixedThreadPool(32); // TODO 별도의 스레드 풀 생성 -> 최대 동시 요청 32개 허용
		CountDownLatch latch = new CountDownLatch(count);

		for (int i = 0; i < count; i++) {
			executorService.submit(() -> {
				try {
					redissonLockStockFacade.decrease(product.getId(), 1L); // TODO 수량 1 감소
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		Product reducedProduct = productRepository.findById(product.getId()).orElseThrow();
		assertEquals(0, reducedProduct.getStock()); // 100 - (1 * 100) = 0

		log.info("테스트 진행 시간 : {} 초", (System.currentTimeMillis() - startTime) / 1000.0);
	}

}
package com.start.portfolio.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.start.portfolio.entity.Stock;
import com.start.portfolio.facade.LettuceLockStockFacade;
import com.start.portfolio.facade.OptimisticLockStockFacade;
import com.start.portfolio.facade.RedissonLockStockFacade;
import com.start.portfolio.repository.StockRepository;
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
	private OptimisticLockStockFacade optimisticLockStockFacade;
	@Autowired
	private StockRepository stockRepository;
	@Autowired
	private LettuceLockStockFacade lettuceLockStockFacade;
	@Autowired
	private RedissonLockStockFacade redissonLockStockFacade;

	@BeforeEach
	public void before() {
		stockRepository.saveAndFlush(Stock.builder()
			.quantity(100L)
			.productId(1L)
			.build());
	}

	@AfterEach
	public void after() {
		stockRepository.deleteAll();
	}

	@Test
	@DisplayName("재고 감소 - 1개의 요청")
	public void decreaseStock() {
		stockService.decrease(1L, 1L); // TODO 수량 1 감소

		Stock stock = stockRepository.findById(1L).orElseThrow();

		assertEquals(99, stock.getQuantity());
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

		Stock stock = stockRepository.findById(1L).orElseThrow();
		assertEquals(0, stock.getQuantity()); // 100 - (1 * 100) = 0

		log.info("테스트 진행 시간 : {} 초", (System.currentTimeMillis() - startTime) / 1000.0);
	}

	@Test
	@DisplayName("재고 감소 - 1개의 요청 (redis - lettuce, redisson")
	public void decreaseStock_100_2() throws InterruptedException {
		int count = 100;
		long startTime = System.currentTimeMillis();

		ExecutorService executorService = Executors.newFixedThreadPool(32); // TODO 별도의 스레드 풀 생성 -> 최대 동시 요청 32개 허용
		CountDownLatch latch = new CountDownLatch(count);

		for (int i = 0; i < count; i++) {
			executorService.submit(() -> {
				try {
					redissonLockStockFacade.decrease(1L, 1L); // TODO 수량 1 감소
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		Stock stock = stockRepository.findById(1L).orElseThrow();
		assertEquals(0, stock.getQuantity()); // 100 - (1 * 100) = 0

		log.info("테스트 진행 시간 : {} 초", (System.currentTimeMillis() - startTime) / 1000.0);
	}

}
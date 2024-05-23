package com.start.portfolio.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisLockRepository {

	private final RedisTemplate<String, String> redisTemplate;

	// TODO LOCK 메서드 (setnx 1 lock)
	public Boolean lock(Long key) {
		return redisTemplate
			.opsForValue()
			.setIfAbsent(generateKey(key), "lock", Duration.ofMillis(3_000)); // TODO KEY -> stockId 를, VALUE -> lock 문자열
	}

	// TODO UNLOCK 메서드 (del 1)
	public Boolean unlock(Long key) {
		return redisTemplate.delete(generateKey(key));
	}

	private String generateKey(Long key) {
		return key.toString();
	}

	// TODO -> Redis 도 optimistic (while) 이나, named lock 처럼 로직 실행 전후로 락 회득, 해제를 수행해야하기 때문에 facade 로직 추가
}

package com.start.portfolio.redis.publisher;

import com.start.portfolio.kafka.dto.AlarmEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPublisher {

	private final RedisTemplate<String, Object> redisTemplate;

	public void send(AlarmEvent event) {
		log.info("Redis publish started");
		redisTemplate.convertAndSend("alarm", event);
		log.info("Redis publish finished");
	}
}

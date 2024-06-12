package com.start.portfolio.kafka.consumer;

import com.start.portfolio.kafka.dto.AlarmEvent;
import com.start.portfolio.redis.publisher.RedisPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmConsumer {

	private final RedisPublisher redisPublisher;

	//TODO Kafka 브로커에서 해당 토픽에 새로운 메시지가 도착하면, Spring Kafka 가 아래 메서드 호출하여 메시지 처리
	@KafkaListener(topics = "alarm", groupId = "alarm-group")
	public void consumeAlarm(AlarmEvent event, Acknowledgment ack) {
		try {
			log.info("consume the event {}", event);
			redisPublisher.send(event);
			ack.acknowledge(); // 처리 완료를 Kafka에 알림
		} catch (Exception e) {
			log.error("Error processing event: ", e);
			// 예외를 처리할 방법을 추가 (예: 재시도 로직, 대체 처리 등)
		}
	}
}

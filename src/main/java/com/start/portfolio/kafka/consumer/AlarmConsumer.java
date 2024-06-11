package com.start.portfolio.kafka.consumer;

import com.start.portfolio.kafka.dto.AlarmEvent;
import com.start.portfolio.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmConsumer {

	private final AlarmService alarmService;

	//TODO Kafka 브로커에서 해당 토픽에 새로운 메시지가 도착하면, Spring Kafka 가 아래 메서드 호출하여 메시지 처리
	@KafkaListener(topics = "alarm")
	public void consumeAlarm(AlarmEvent event, Acknowledgment ack) {
		log.info("consume the event {}", event);
		alarmService.send(event.getAlarmType(), event.getAlarmArgs(), event.getReceiverUserId());
		ack.acknowledge(); // 완료 시 ack
	}
}

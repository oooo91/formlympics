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

	//TODO 알람 이벤트 및 ack 받음 -> alarmService 에 send
	@KafkaListener(topics = "alarm")
	public void consumeAlarm(AlarmEvent event, Acknowledgment ack) {
		log.info("consume the event {}", event);
		alarmService.send(event.getAlarmType(), event.getAlarmArgs(), event.getReceiverUserId());
		// 완료 시 ack
		ack.acknowledge();
	}
}

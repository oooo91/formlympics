package com.start.portfolio.kafka.producer;

import com.start.portfolio.kafka.dto.AlarmEvent;
import com.start.portfolio.kafka.enums.AlarmTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmProducer {

	private final KafkaTemplate<Long, AlarmEvent> kafkaTemplate;

	public void send(AlarmTopic topic, AlarmEvent event) {
		kafkaTemplate.send(topic.name(), event.getReceiverUserId(), event);
		log.info("Send to kafka finished");
	}

}

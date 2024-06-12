package com.start.portfolio.kafka.config;

import com.start.portfolio.kafka.dto.AlarmEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

// TODO producerFactory -> kafka 연결, kafkaTemplate 생성 -> 메시지 발행
// TODO consumerFactory , kafkaListenerContainerFactory -> kafka 메시지 읽기
@EnableKafka
@Configuration
class KafkaConfig {

	private static final String BOOTSTRAP_SERVER = "localhost:9092";

	@Bean
	public ProducerFactory<Long, AlarmEvent> producerFactory() {
		Map<String, Object> configurationProperties = new HashMap<>();
		configurationProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
		configurationProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
			LongSerializer.class);
		configurationProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
			JsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(configurationProperties);
	}

	@Bean
	public ConsumerFactory<Long, AlarmEvent> consumerFactory() {
		Map<String, Object> configurationProperties = new HashMap<>();
		configurationProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
		configurationProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "alarm-group");
		configurationProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		configurationProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
			LongDeserializer.class);
		configurationProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
			JsonDeserializer.class);
		configurationProperties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
			JsonDeserializer.class.getName());
		//JSON 역직렬화 시 신뢰할 수 있는 패키지 설정
		configurationProperties.put(JsonDeserializer.TRUSTED_PACKAGES,
			"com.start.portfolio.kafka.dto,com.start.portfolio.entity.args,com.start.portfolio.enums");
		// ErrorHandlingDeserializer 설정 추가
		configurationProperties.put(JsonDeserializer.VALUE_DEFAULT_TYPE, AlarmEvent.class.getName());

		return new DefaultKafkaConsumerFactory<>(configurationProperties);
	}

	@Bean
	public KafkaTemplate<Long, AlarmEvent> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	//topic 에 대한 listener
	@Bean
	public ConcurrentKafkaListenerContainerFactory<Long, AlarmEvent> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<Long, AlarmEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
		return factory;
	}
}

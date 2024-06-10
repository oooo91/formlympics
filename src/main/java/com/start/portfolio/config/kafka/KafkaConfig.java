package com.start.portfolio.config.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

@EnableKafka
@Configuration
class KafkaConfig {

	private static final String BOOTSTRAP_SERVER = "localhost:9092";

	@Bean
	public ProducerFactory<String, String> producerFactory() {
		Map<String, Object> configurationProperties = new HashMap<>();
		configurationProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
		configurationProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configurationProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return new DefaultKafkaProducerFactory<>(configurationProperties);
	}

	@Bean
	public ConsumerFactory<String, String> consumerFactory() {
		Map<String, Object> configurationProperties = new HashMap<>();
		configurationProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
		configurationProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "fintech");
		configurationProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		configurationProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configurationProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		return new DefaultKafkaConsumerFactory<>(configurationProperties);
	}
}

package com.start.portfolio.util.converter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.start.portfolio.entity.args.AlarmArgs;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter(autoApply = true)
public class AlarmArgsConverter implements AttributeConverter<AlarmArgs, String> {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(AlarmArgs alarmArgs) {
		try {
			return objectMapper.writeValueAsString(alarmArgs);
		} catch (JsonProcessingException e) {
			log.info("왜 오류가 나냐고오오오오!!!!!!!!!!!!!!!");
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public AlarmArgs convertToEntityAttribute(String json) {
		try {
			return objectMapper.readValue(json, AlarmArgs.class);
		} catch (JsonProcessingException e) {
			log.info("왜 오류가 나냐고오오오오");
			log.info(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}
}

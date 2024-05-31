package com.start.portfolio.util.converter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.start.portfolio.entity.args.AlarmArgs;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AlarmArgsConverter implements AttributeConverter<AlarmArgs, String> {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(AlarmArgs alarmArgs) {
		try {
			return objectMapper.writeValueAsString(alarmArgs);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public AlarmArgs convertToEntityAttribute(String json) {
		try {
			return objectMapper.readValue(json, AlarmArgs.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}

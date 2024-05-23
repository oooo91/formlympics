package com.start.portfolio.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.start.portfolio.entity.Education;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;

@Converter
@RequiredArgsConstructor
public class EducationListJsonConverter implements AttributeConverter<List<Education>, String> {

	private final ObjectMapper objectMapper;

	// 자바 객체를 데이터베이스에 저장할 때 호출한다. H2 는 TEXT 만 저장 가능하기 때문이다.
	@Override
	public String convertToDatabaseColumn(List<Education> attribute) {

		if (Objects.isNull(attribute)) {
			return Strings.EMPTY; // null 반환 보다는 공백 문자 반환
		}
		try {
			return objectMapper.writeValueAsString(attribute); //객체 값을 String 으로 변경
		} catch (JsonProcessingException e) {
			// TODO 에러 핸들링 구현 예정
			throw new RuntimeException("error converting list to json", e);
		}
	}

	// 데이터베이스로부터 값을 읽을 때 호출된다. List<Education> 객체로 변환하여 반환한다.
	@Override
	public List<Education> convertToEntityAttribute(String dbData) {

		if (Strings.isBlank((dbData))) {
			return Collections.emptyList();
		}
		try {
			return objectMapper.readValue(dbData, new TypeReference<>() { //TypeReference -> Jackson 에서 제공하는 제네릭 타입을 활용하도록 함
			});
		} catch (JsonProcessingException e) {
			// TODO 에러 핸들링 구현 예정
			throw new RuntimeException("error converting json to list", e);
		}
	}
}

package com.start.portfolio.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.start.portfolio.dto.ProductDto.Request;
import com.start.portfolio.entity.Alarm;
import com.start.portfolio.entity.Form;
import com.start.portfolio.entity.User;
import com.start.portfolio.entity.args.AlarmArgs;
import com.start.portfolio.enums.AlarmType;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class AlarmDto {

	public record Request(
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
		LocalDateTime registeredAt,
		AlarmType alarmType
	) {}

	@Builder
	@Getter
	@Setter
	public static class Response {
		private Long id;
		private User user;
		private AlarmType alarmType;
		private AlarmArgs alarmArgs;
		private LocalDateTime registeredAt;
	}
}
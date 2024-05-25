package com.start.portfolio.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.start.portfolio.entity.Form;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class FormDto {

	public record Request(
		String title,
		String content,
		Long likes,
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
		LocalDateTime depositStartDate,
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
		LocalDateTime depositEndDate,
		List<ProductDto.Request> productList
	) {
		public Form toEntity() {
			return Form.builder()
				.title(title)
				.content(content)
				.likes(likes)
				.depositStartDate(depositStartDate)
				.depositEndDate(depositEndDate)
				.build();
		}
	}

	@Builder
	@Getter
	@Setter
	public static class Response {
		private String title;
		private String content;
		private Long likes;
		private LocalDateTime depositStartDate;
		private LocalDateTime depositEndDate;
		private List<ProductDto.Response> productList;
	}
}

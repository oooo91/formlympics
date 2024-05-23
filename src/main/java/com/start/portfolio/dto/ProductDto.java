package com.start.portfolio.dto;

import lombok.Builder;
import lombok.Getter;

public class ProductDto {
	// 레코드 -> getter 자동 생성, final 자동 생성
	public record Request(
		String productName,
		Long stock,
		Long price
	) {}

	// Response 객체는 record 가 아닌 inner class 로 작성, record 는 불변(immutable) 이기 때문에 빌더 패턴을 사용할 수 없다.
	@Builder
	@Getter
	public static class Response {
		private Long productId;
		private String productName;
		private Long stock;
		private Long price;
	}
}

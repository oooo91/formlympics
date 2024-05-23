package com.start.portfolio.dto.order;

import lombok.Builder;
import lombok.Getter;

public class ProductDto {

	public record Request(
		String productName,
		Long stock,
		Long price
	) {}

	@Builder
	@Getter
	public static class Response {
		private Long productId;
		private String productName;
		private Long stock;
		private Long price;
	}
}

package com.start.portfolio.dto;

public class OrdersDto {

	public record Request(
		Long productId,
		Long quantity,
		String depositName
	) {}

}

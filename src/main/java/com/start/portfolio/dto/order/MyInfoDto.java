package com.start.portfolio.dto.order;

import lombok.Builder;
import lombok.Getter;

public class MyInfoDto {

	@Builder
	@Getter
	public static class Response {
		private UserDto.Response user;
		private AddressDto.Response address;
		private RefundDto.Response refund;
	}
}

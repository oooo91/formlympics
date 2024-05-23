package com.start.portfolio.dto;

import com.start.portfolio.entity.Address;
import lombok.Builder;
import lombok.Getter;

public class AddressDto {

	public record Request(
		String address1,
		String address2,
		String zipcode
	) {
		public Address toEntity() {
			return Address.builder()
				.address1(address1)
				.address2(address2)
				.zipcode(zipcode)
				.build();
		}
	}

	@Builder
	@Getter
	public static class Response {
		private String address1;
		private String address2;
		private String zipcode;
	}
}

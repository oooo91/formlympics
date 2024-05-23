package com.start.portfolio.dto;

import com.start.portfolio.entity.User;
import lombok.Builder;
import lombok.Getter;

public class UserDto {

	public record Request(
		String name,
		String email,
		String password,
		String phone
	) {
		public User toEntity() {
			return User.builder()
				.name(name)
				.email(email)
				.phone(phone)
				.build();
		}
	}

	@Builder
	@Getter
	public static class Response {
		private String name;
		private String email;
		private String phone;
	}
}

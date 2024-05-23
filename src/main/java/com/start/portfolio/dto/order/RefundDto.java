package com.start.portfolio.dto.order;

import com.start.portfolio.entity.order.Refund;
import com.start.portfolio.enums.order.Bank;
import lombok.Builder;
import lombok.Getter;

public class RefundDto {

	public record Request(
		Bank bank,
		String accountNumber,
		String accountHolderName
	) {
		public Refund toEntity() {
			return Refund.builder()
				.bank(bank)
				.accountNumber(accountNumber)
				.accountHolderName(accountHolderName)
				.build();
		}
	}

	@Builder
	@Getter
	public static class Response {
		private Bank bank;
		private String accountNumber;
		private String accountHolderName;
	}
}

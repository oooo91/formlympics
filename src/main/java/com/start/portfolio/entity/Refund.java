package com.start.portfolio.entity;

import com.start.portfolio.dto.RefundDto;
import com.start.portfolio.enums.Bank;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Refund {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "refund_id")
	private Long id;

	@OneToOne(mappedBy = "refund", fetch = FetchType.LAZY)
	private User user;

	@Enumerated(EnumType.STRING)
	private Bank bank;
	private String accountNumber;
	private String accountHolderName;

	@Builder
	Refund(
		Bank bank,
		String accountNumber,
		String accountHolderName
	) {
		this.bank = bank;
		this.accountNumber = accountNumber;
		this.accountHolderName = accountHolderName;
	}

	public RefundDto.Response toDto() {
		return RefundDto.Response.builder()
			.accountHolderName(this.accountHolderName)
			.accountNumber(this.accountNumber)
			.bank(this.bank)
			.build();
	}
}

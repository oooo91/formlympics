package com.start.portfolio.entity.order;

import com.start.portfolio.dto.order.AddressDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "address_id")
	private Long id;

	@OneToOne(mappedBy = "address", fetch = FetchType.LAZY)
	private User user;

	private String address1;
	private String address2;
	private String zipcode;

	@Builder
	Address(
		String address1,
		String address2,
		String zipcode
	) {
		this.address1 = address1;
		this.address2 = address2;
		this.zipcode = zipcode;
	}

	public AddressDto.Response toDto() {
		return AddressDto.Response.builder()
			.address1(this.address1)
			.address2(this.address2)
			.zipcode(this.zipcode)
			.build();
	}
}

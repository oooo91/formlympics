package com.start.portfolio.entity;

import com.start.portfolio.dto.UserDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;
	private String name;
	private String email;
	private String password;
	private String phone;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "refund_id")
	private Refund refund;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "address_id")
	private Address address;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Orders> orders;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<UserCoupon> userCoupons;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Form> form;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Cart> likes;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@Builder
	User(
		String name,
		String email,
		String phone
	) {
		this.name = name;
		this.email = email;
		this.phone = phone;
	}

	public UserDto.Response toDto() {
		return UserDto.Response.builder()
			.email(this.email)
			.name(this.name)
			.phone(this.phone)
			.build();
	}

}

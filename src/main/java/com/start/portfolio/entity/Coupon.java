package com.start.portfolio.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Coupon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "coupon_id")
	private Long id;

	@OneToMany(mappedBy = "coupon", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<UserCoupon> userCoupons; //List 는 중복 허용, Set 은 중복 허용 x -> 동일한 쿠폰이 동일한 사용자에게 여러 번 할당되는 것을 방지

	private String couponName;
	private Long couponAmount; //쿠폰 금액
	private Long totalCouponQuantity; //쿠폰 수량
	private Long issuedCouponCount; //발급된 쿠폰 수
	public void update() {
		this.issuedCouponCount = issuedCouponCount + 1;
	}

	@Builder
	Coupon(
		String couponName,
		Long couponAmount,
		Long totalCouponQuantity,
		Long issuedCouponCount
	){
		this.couponName = couponName;
		this.couponAmount = couponAmount;
		this.totalCouponQuantity = totalCouponQuantity;
		this.issuedCouponCount = issuedCouponCount;
	}
}

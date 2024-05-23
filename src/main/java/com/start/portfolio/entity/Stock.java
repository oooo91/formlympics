package com.start.portfolio.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long productId;
	private Long quantity;

	/*
	@Version // TODO 낙관적 락 사용
	private Long version;
	*/

	@Builder
	Stock(
		Long productId,
		Long quantity
	) {
		this.productId = productId;
		this.quantity = quantity;
	}

	//TODO 재고 감소 메서드
	public void decrease(Long quantity) {
		if (this.quantity - quantity < 0) {
			throw new RuntimeException("재고는 0개 미만이 될 수 없습니다.");
		}
		this.quantity -= quantity;
	}
}

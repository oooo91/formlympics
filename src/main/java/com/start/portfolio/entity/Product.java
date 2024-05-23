package com.start.portfolio.entity;

import com.start.portfolio.dto.ProductDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "form_id")
	private Form form;

	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
	private List<Orders> orders;

	private String productName;
	private Long stock;
	private Long price;

	// TODO Dto 로 파싱
	public ProductDto.Response toDto() {
		return ProductDto.Response.builder()
			.productId(this.id)
			.productName(this.productName)
			.stock(this.stock)
			.price(this.price)
			.build();
	}

	//TODO 재고 감소 메서드
	public void decrease(Long stock) {
		if (this.stock - stock < 0) {
			throw new RuntimeException("재고는 0개 미만이 될 수 없습니다.");
		}
		this.stock -= stock;
	}
}

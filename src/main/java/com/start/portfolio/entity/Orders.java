package com.start.portfolio.entity;

import com.start.portfolio.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orders {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	private Long quantity;
	private Long totalPrice;

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

	@CreationTimestamp
	private LocalDateTime orderDate;

	private String depositName;
	private LocalDateTime depositDate;

	@Builder
	Orders(
		String depositName,
		OrderStatus orderStatus,
		Long quantity,
		Long totalPrice,
		Product product,
		User user
	) {
		this.depositName = depositName;
		this.orderStatus = orderStatus;
		this.quantity = quantity;
		this.totalPrice = totalPrice;
		this.product = product;
		this.user = user;
	}
}

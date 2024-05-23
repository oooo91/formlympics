package com.start.portfolio.repository.order;

import com.start.portfolio.entity.order.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

}

package com.start.portfolio.repository.order;

import com.start.portfolio.entity.order.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

}

package com.start.portfolio.repository.order;

import com.start.portfolio.entity.order.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findAllByFormId(Long formId);
}

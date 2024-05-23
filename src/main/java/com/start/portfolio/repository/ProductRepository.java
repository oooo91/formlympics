package com.start.portfolio.repository;

import com.start.portfolio.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findAllByFormId(Long formId);
}

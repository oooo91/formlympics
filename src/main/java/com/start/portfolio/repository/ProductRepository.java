package com.start.portfolio.repository;

import com.start.portfolio.entity.Product;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findAllByFormId(Long formId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select p from Product p where p.id = :id")
	Optional<Product> findByIdWithPessimisticLock(Long id);
}

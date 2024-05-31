package com.start.portfolio.repository;

import com.start.portfolio.entity.Cart;
import com.start.portfolio.entity.Form;
import com.start.portfolio.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

	Optional<Cart> findByUserIdAndFormId(Long userId, Long formId);
}

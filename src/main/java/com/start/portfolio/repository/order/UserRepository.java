package com.start.portfolio.repository.order;

import com.start.portfolio.entity.order.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);
}
